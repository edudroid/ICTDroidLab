package hu.edudroid.ict.plugins.test;

import java.util.ArrayList;
import java.util.List;

import hu.edudroid.ict.plugins.PluginDescriptor;
import hu.edudroid.ict.plugins.PluginManager;
import hu.edudroid.interfaces.Plugin;
import junit.framework.TestCase;

public class PluginManagerTest extends TestCase {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	/**
	 * Tests if manager is capable of merging disjunct lists
	 */
	public void testAppend() {
		List<PluginDescriptor> availablePlugins = new ArrayList<PluginDescriptor>();
		PluginDescriptor first = new PluginDescriptor("First Plugin", "hu.edudroid.first", "Some description");
		PluginDescriptor second = new PluginDescriptor("Second Plugin", "hu.edudroid.second", "Some description");
		PluginDescriptor third = new PluginDescriptor("Third Plugin", "hu.edudroid.third", "Some description");
		availablePlugins.add(first);
		availablePlugins.add(second);
		availablePlugins.add(third);
		List<Plugin> downloadedPlugins = new ArrayList<Plugin>();
		Plugin fourth = new MockPlugin("Fourth", "hu.edudroid.fourth");
		Plugin fifth = new MockPlugin("Fifth", "hu.edudroid.fifth");
		downloadedPlugins.add(fourth);
		downloadedPlugins.add(fifth);
		List<PluginDescriptor> result = PluginManager.getAvailablePlugins(availablePlugins, downloadedPlugins);
		assertEquals(5, result.size());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}	
}
