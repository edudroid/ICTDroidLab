package hu.edudroid.interfaces;

public class Constants {

	
	public static final String INTENT_ACTION_CALL_METHOD 				= "hu.edudroid.ict.plugin.callmethod";
	public static final String INTENT_ACTION_DESCRIBE 					= "hu.edudroid.ict.plugin_polling_answer";
	public static final String INTENT_ACTION_PLUGIN_POLL				= "hu.edudroid.ict.plugin_polling_question";
	public static final String INTENT_ACTION_PLUGIN_CALLMETHOD_ANSWER	= "hu.edudroid.ict.plugin_callmethod_answer";
	public static final String INTENT_ACTION_PLUGIN_EVENT				= "hu.edudroid.ict.plugin_event";
	
	public static final String INTENT_EXTRA_KEY_DESCRIBE_TYPE = "describe type";
	public static final String INTENT_EXTRA_KEY_PLUGIN_ID = "plugin";
	public static final String INTENT_EXTRA_KEY_PLUGIN_AUTHOR = "author";
	public static final String INTENT_EXTRA_KEY_DESCRIPTION = "description";
	public static final String INTENT_EXTRA_KEY_VERSION = "version";
	public static final String INTENT_EXTRA_KEY_PLUGIN_METHODS = "methods";
	public static final String INTENT_EXTRA_KEY_PLUGIN_EVENTS = "events";
	public static final String INTENT_EXTRA_KEY_ERROR_MESSAGE = "error message";
	public static final String INTENT_EXTRA_KEY_EVENT_NAME = "event name";
	
	public static final String INTENT_EXTRA_CALL_ID 					= "id";
	public static final String INTENT_EXTRA_METHOD_NAME 				= "methodname";
	public static final String INTENT_EXTRA_METHOD_PARAMETERS 			= "methodparams";
	
	public static final String INTENT_EXTRA_VALUE_RESULT = "report result";
	public static final String INTENT_EXTRA_VALUE_ERROR = "report error";
	public static final String INTENT_EXTRA_VALUE_REPORT = "report self";
	public static final String INTENT_EXTRA_VALUE_EVENT = "report event";
}
