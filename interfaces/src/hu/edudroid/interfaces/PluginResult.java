package hu.edudroid.interfaces;

import java.util.Map;

/**
 * Helper class for the framework to manage quota consumption of the plugins
 * @author lajthabalazs
 *
 */
public class PluginResult {
	private Map<String, Object> result;
	private Map<Long, Double> consumedQuota;
	
	/** @param consumedQuota should be null if no quota was consumed */
	public PluginResult(Map<String, Object> result, Map<Long, Double> consumedQuota) {
		this.result = result;
		this.consumedQuota = consumedQuota;
	}

	public Map<String, Object> getResult() {
		return result;
	}

	public Map<Long, Double> getConsumedQuota() {
		return consumedQuota;
	}
}
