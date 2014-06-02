package hu.edudroid.ict_plugin_cell;

import java.util.HashMap;
import java.util.Map;

import hu.edudroid.ictplugin.PluginCommunicationInterface;
import hu.edudroid.interfaces.CellConstants;
import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

public class ICTPhoneStateListener extends PhoneStateListener {
    CellService service;
	private TelephonyManager telephonyManager;
	PluginCommunicationInterface pluginCommunicationInterface;

    public ICTPhoneStateListener(CellService ictCellService) {
    	this.service = ictCellService;
		telephonyManager = (TelephonyManager)service.getSystemService(Context.TELEPHONY_SERVICE);
		int flags = PhoneStateListener.LISTEN_SIGNAL_STRENGTH | 
				PhoneStateListener.LISTEN_CELL_LOCATION |
				PhoneStateListener.LISTEN_DATA_CONNECTION_STATE |
				PhoneStateListener.LISTEN_SERVICE_STATE;
		telephonyManager.listen(this, flags);
		pluginCommunicationInterface = new PluginCommunicationInterface(new CellPlugin());
	}

	@Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
		Map<String, Object> result = new HashMap<String, Object>();
        int signalStrengthValue;
		if (signalStrength.isGsm()) {
			result.put(CellConstants.KEY_NETWORK_TECHNOLOGY, "GSM");
            if (signalStrength.getGsmSignalStrength() != 99)
                signalStrengthValue = signalStrength.getGsmSignalStrength() * 2 - 113;
            else
                signalStrengthValue = signalStrength.getGsmSignalStrength();
        } else {
        	result.put(CellConstants.KEY_NETWORK_TECHNOLOGY, "CDMA");
        	signalStrengthValue = signalStrength.getCdmaDbm();
        }
		result.put(CellConstants.KEY_SIGNAL_STRENGTH, signalStrengthValue);
		pluginCommunicationInterface.fireEvent(CellConstants.EVENT_SIGNAL_STRENGTH_CHANGED, result , service);
    }
	
	@Override
	public void onCellLocationChanged(CellLocation location) {
		if (location instanceof GsmCellLocation) {
			GsmCellLocation gsmLocation = (GsmCellLocation)location;
			gsmLocation.getCid();
			gsmLocation.getLac();
			Map<String, Object> result = new HashMap<String, Object>();
			pluginCommunicationInterface.fireEvent(CellConstants.EVENT_SIGNAL_STRENGTH_CHANGED, result , service);
		} else if (location instanceof CdmaCellLocation){
			CdmaCellLocation cdmaLocation = (CdmaCellLocation)location;
			cdmaLocation.getBaseStationId();
			cdmaLocation.getNetworkId();
			Map<String, Object> result = new HashMap<String, Object>();
			pluginCommunicationInterface.fireEvent(CellConstants.EVENT_SIGNAL_STRENGTH_CHANGED, result , service);
		}
	}
	
	@Override
	public void onDataConnectionStateChanged(int state) {
		
	}
	
	@Override
	public void onServiceStateChanged(ServiceState serviceState) {
	}

	public void disconnect() {
		telephonyManager.listen(this, PhoneStateListener.LISTEN_NONE);
	}

}
