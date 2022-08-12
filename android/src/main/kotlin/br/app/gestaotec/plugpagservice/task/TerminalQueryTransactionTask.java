package br.app.gestaotec.plugpagservice.task;


import android.os.AsyncTask;
import androidx.annotation.NonNull;

import br.com.uol.pagseguro.plugpag.PlugPag;
import br.com.uol.pagseguro.plugpag.PlugPagDevice;
import br.com.uol.pagseguro.plugpag.PlugPagTransactionResult;
import br.app.gestaotec.plugpagservice.PlugPagManager;
import br.app.gestaotec.plugpagservice.TaskHandler;
import br.app.gestaotec.plugpagservice.helper.Bluetooth;

public class TerminalQueryTransactionTask extends AsyncTask<Void, Void, PlugPagTransactionResult> {

    // -----------------------------------------------------------------------------------------------------------------
    // Instance attributes
    // -----------------------------------------------------------------------------------------------------------------

    private TaskHandler mHandler = null;

    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new void payment task.
     *
     * @param handler Handler used to report updates.
     */
    public TerminalQueryTransactionTask(@NonNull TaskHandler handler) {
        if (handler == null) {
            throw new RuntimeException("TaskHandler reference cannot be null");
        }

        this.mHandler = handler;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Task execution
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mHandler.onTaskStart();
    }

    @Override
    protected PlugPagTransactionResult doInBackground(Void... args) {
        PlugPagTransactionResult result = null;
        PlugPag plugpag = null;

        plugpag = PlugPagManager.getInstance().getPlugPag();
        plugpag.initBTConnection(new PlugPagDevice(Bluetooth.getTerminal()));
        result = plugpag.getLastApprovedTransaction();

        return result;
    }

    @Override
    protected void onPostExecute(PlugPagTransactionResult plugPagTransactionResult) {
        super.onPostExecute(plugPagTransactionResult);
        this.mHandler.onTaskFinished(plugPagTransactionResult);
        this.mHandler = null;
    }
}