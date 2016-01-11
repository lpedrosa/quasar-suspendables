import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;

import com.github.lpedrosa.AsyncClient;
import com.github.lpedrosa.Callback;
import com.github.lpedrosa.ClientException;
import com.github.lpedrosa.SyncClient;

public class FiberSyncClient implements SyncClient {

    private final AsyncClient asyncClient;

    private FiberSyncClient(AsyncClient asyncClient) {
        this.asyncClient = asyncClient;
    }

    public static FiberSyncClient wrap(AsyncClient client) {
        return new FiberSyncClient(client);
    }

    private static abstract class FiberCallback extends FiberAsync<String, ClientException> implements Callback {
        @Override
        public void onSuccess(String value) {
            asyncCompleted(value);
        }

        @Override
        public void onError(ClientException ex) {
            asyncFailed(ex);
        }
    }

    @Override
    @Suspendable
    public String getValue(final int argument) throws ClientException {
        try {
            return new FiberCallback() {
                @Override
                protected void requestAsync() {
                    FiberSyncClient.this.asyncClient.getValue(argument, this);
                }
            }.run();
        } catch (SuspendExecution suspendExecution) {
            throw new AssertionError("Shouldn't happen");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
