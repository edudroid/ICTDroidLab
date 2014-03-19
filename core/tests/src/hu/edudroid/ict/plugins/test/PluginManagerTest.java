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

	/**
	 * Tests if manager is capable of merging overlapping lists
	 */
	public void testMerge() {
		List<PluginDescriptor> availablePlugins = new ArrayList<PluginDescriptor>();
		PluginDescriptor firstDescriptor = new PluginDescriptor("First Plugin", "hu.edudroid.first", "Some description");
		PluginDescriptor secondDescriptor = new PluginDescriptor("Second Plugin", "hu.edudroid.second", "Some description");
		PluginDescriptor thirdDescriptor = new PluginDescriptor("Third Plugin", "hu.edudroid.third", "Some description");
		PluginDescriptor fourthDescriptor = new PluginDescriptor("Fourth Plugin", "hu.edudroid.fourth", "Some description");
		PluginDescriptor fifthDescriptor = new PluginDescriptor("Fifth Plugin", "hu.edudroid.fifth", "Some description");
		availablePlugins.add(firstDescriptor);
		availablePlugins.add(secondDescriptor);
		availablePlugins.add(thirdDescriptor);
		availablePlugins.add(fourthDescriptor);
		availablePlugins.add(fifthDescriptor);
		List<Plugin> downloadedPlugins = new ArrayList<Plugin>();
		Plugin fourthPlugin = new MockPlugin("Fourth Plugin", "hu.edudroid.fourth");
		Plugin fifthPlugin = new MockPlugin("Fifth Plugin", "hu.edudroid.fifth");
		downloadedPlugins.add(fourthPlugin);
		downloadedPlugins.add(fifthPlugin);
		List<PluginDescriptor> result = PluginManager.getAvailablePlugins(availablePlugins, downloadedPlugins);
		assertEquals(5, result.size());
	}

	/**
	 * Tests if manager is capable of merging a null descriptor list
	 */
	public void testNoDescriptors() {
		List<PluginDescriptor> availablePlugins = null;
		List<Plugin> downloadedPlugins = new ArrayList<Plugin>();
		Plugin fourthPlugin = new MockPlugin("Fourth Plugin", "hu.edudroid.fourth");
		Plugin fifthPlugin = new MockPlugin("Fifth Plugin", "hu.edudroid.fifth");
		downloadedPlugins.add(fourthPlugin);
		downloadedPlugins.add(fifthPlugin);
		List<PluginDescriptor> result = PluginManager.getAvailablePlugins(availablePlugins, downloadedPlugins);
		assertEquals(2, result.size());
	}
	
	/**
	 * Tests if manager is capable of merging when there are no installed plugins
	 */
	public void testNoPlugins() {
		List<PluginDescriptor> availablePlugins = new ArrayList<PluginDescriptor>();
		PluginDescriptor firstDescriptor = new PluginDescriptor("First Plugin", "hu.edudroid.first", "Some description");
		PluginDescriptor secondDescriptor = new PluginDescriptor("Second Plugin", "hu.edudroid.second", "Some description");
		PluginDescriptor thirdDescriptor = new PluginDescriptor("Third Plugin", "hu.edudroid.third", "Some description");
		PluginDescriptor fourthDescriptor = new PluginDescriptor("Fourth Plugin", "hu.edudroid.fourth", "Some description");
		availablePlugins.add(firstDescriptor);
		availablePlugins.add(secondDescriptor);
		availablePlugins.add(thirdDescriptor);
		availablePlugins.add(fourthDescriptor);
		List<Plugin> downloadedPlugins = null;
		List<PluginDescriptor> result = PluginManager.getAvailablePlugins(availablePlugins, downloadedPlugins);
		assertEquals(4, result.size());
	}
	
	/**
	 * Tests if manager is capable of merging when there are no installed plugins
	 */
	public void testNoData() {
		List<PluginDescriptor> availablePlugins = null;
		List<Plugin> downloadedPlugins = null;
		List<PluginDescriptor> result = PluginManager.getAvailablePlugins(availablePlugins, downloadedPlugins);
		assertEquals(0, result.size());
	}


	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}	
}
