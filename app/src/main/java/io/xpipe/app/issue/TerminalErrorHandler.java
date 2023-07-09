package io.xpipe.app.issue;

import io.sentry.Sentry;
import io.xpipe.app.core.*;
import io.xpipe.app.core.mode.OperationMode;
import io.xpipe.app.update.XPipeDistributionType;
import io.xpipe.app.util.Hyperlinks;
import io.xpipe.app.util.PlatformState;
import io.xpipe.core.impl.LocalStore;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.concurrent.CountDownLatch;

public class TerminalErrorHandler implements ErrorHandler {

    private final ErrorHandler basic = new LogErrorHandler();

    @Override
    public void handle(ErrorEvent event) {
        basic.handle(event);

        if (!OperationMode.GUI.isSupported() || event.isOmitted()) {
            event.clearAttachments();
            SentryErrorHandler.getInstance().handle(event);
            OperationMode.halt(1);
        }

        handleGui(event);
    }

    private void handleGui(ErrorEvent event) {
        if (PlatformState.getCurrent() == PlatformState.NOT_INITIALIZED) {
            try {
                CountDownLatch latch = new CountDownLatch(1);
                Platform.setImplicitExit(false);
                Platform.startup(latch::countDown);
                try {
                    latch.await();
                    PlatformState.setCurrent(PlatformState.RUNNING);
                } catch (InterruptedException ignored) {
                }
            } catch (Throwable r) {
                // Check if we already exited
                if ("Platform.exit has been called".equals(r.getMessage())) {
                    PlatformState.setCurrent(PlatformState.EXITED);
                    return;
                }

                if ("Toolkit already initialized".equals(r.getMessage())) {
                    PlatformState.setCurrent(PlatformState.RUNNING);
                } else {
                    // Platform initialization has failed in this case
                    event.clearAttachments();
                    handleSecondaryException(event, r);
                    return;
                }
            }
        }

        try {
            AppProperties.init();
            AppState.init();
            AppExtensionManager.init(false);
            AppI18n.init();
            AppStyle.init();
            ErrorHandlerComp.showAndTryWait(event, true);
            Sentry.flush(5000);
        } catch (Throwable r) {
            event.clearAttachments();
            handleSecondaryException(event, r);
            return;
        }

        if (OperationMode.isInStartup()) {
            handleProbableUpdate();
        }

        OperationMode.halt(1);
    }

    private static void handleSecondaryException(ErrorEvent event, Throwable t) {
        SentryErrorHandler.getInstance().handle(event);
        SentryErrorHandler.getInstance().handle(ErrorEvent.fromThrowable(t).build());
        Sentry.flush(5000);
        t.printStackTrace();
        OperationMode.halt(1);
    }

    private static void handleProbableUpdate() {
        // If a terminal error occurred before local shell initialization, we can't make use of any functionality to update
        if (!LocalStore.isLocalShellInitialized()) {
            return;
        }

        try {
            var rel = XPipeDistributionType.get().getUpdateHandler().refreshUpdateCheck();
            if (rel != null && rel.isUpdate()) {
                var update = AppWindowHelper.showBlockingAlert(alert -> {
                            alert.setAlertType(Alert.AlertType.INFORMATION);
                            alert.setTitle(AppI18n.get("updateAvailableTitle"));
                            alert.setHeaderText(AppI18n.get("updateAvailableHeader", rel.getVersion()));
                            alert.getDialogPane()
                                    .setContent(
                                            AppWindowHelper.alertContentText(AppI18n.get("updateAvailableContent")));
                            alert.getButtonTypes().clear();
                            alert.getButtonTypes()
                                    .add(new ButtonType(AppI18n.get("checkOutUpdate"), ButtonBar.ButtonData.YES));
                            alert.getButtonTypes().add(new ButtonType(AppI18n.get("ignore"), ButtonBar.ButtonData.NO));
                        })
                        .map(buttonType -> buttonType.getButtonData().isDefaultButton())
                        .orElse(false);
                if (update) {
                    Hyperlinks.open(rel.getReleaseUrl());
                }
            }
        } catch (Throwable t) {
            SentryErrorHandler.getInstance().handle(ErrorEvent.fromThrowable(t).build());
            Sentry.flush(5000);
            t.printStackTrace();
            OperationMode.halt(1);
        }
    }
}
