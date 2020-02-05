package com.U1764486.utils;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;

import java.rmi.RemoteException;

/**
 * A generic implementation to create a RemoteEventListener
 * easily, without having to explicitly define an Exporter.
 * This is inherited by any notifiers used inside the app.
 */
public class Notifier implements RemoteEventListener {


    protected Exporter remoteExporter;
    protected RemoteEventListener listener;
    protected Notifier() {
        try{
            remoteExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
                            new BasicILFactory(), false, true);
            listener = (RemoteEventListener) remoteExporter.export(this);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public RemoteEventListener Listen(){
        return listener;
    }

    @Override
    public void notify(RemoteEvent remoteEvent)
            throws UnknownEventException, RemoteException {
        super.notify();
    }
}
