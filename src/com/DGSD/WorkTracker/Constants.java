package com.DGSD.WorkTracker;

public class Constants {

	public static class Authority {
		public static final String ITEM_PROVIDER = "com.DGSD.WorkTracker.Data.Provider.ItemProvider";
	}

	public static class Permissions {
		public static final String RECEIVE_BROADCASTS = "com.DGSD.WorkTracker.receive_broadcast_permission";
		public static final String DATABASE_ACCESS = "com.DGSD.WorkTracker.database_access";
	}

	public static class ResultType {
		public static final String SUCCESS = "com.DGSD.WorkTracker.success";
		public static final String NO_RESULT = "com.DGSD.WorkTracker.no_result";
		public static final String ERROR = "com.DGSD.WorkTracker.error";
	}

	public static class Extra {
		public static final String ID = "com.DGSD.WorkTracker.id";
		public static final String URI = "com.DGSD.WorkTracker.uri";
		public static final String DATA_TYPE = "com.DGSD.WorkTracker.data_type";
		public static final String CONTENT_VALUES = "com.DGSD.WorkTracker.content_values";
		public static final String FIELDS = "com.DGSD.WorkTracker.fields";
		public static final String ITEM = "com.DGSD.WorkTracker.item_entity";
		public static final String REQUEST_CODE = "com.DGSD.WorkTracker.request_code";

	}
}
