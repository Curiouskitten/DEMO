package com.itheima.im3.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

public class ContactProvider extends ContentProvider {

	// Canonical全部类名 com.itheima.im3.core.ContactProvider

	private static final String authority = ContactProvider.class.getCanonicalName();

	// ①　创建Provider 1.继承 2.重写 3.注册 4.使用
	// ②　设计表结构
	// BaseColumns带id的字段接口
	public static class Contact implements BaseColumns {
		public static final String ACCOUNT = "account";
		public static final String NICK = "nick";
		public static final String AVATAR = "avatar";
		public static final String SORT = "sort";// 拼音
	}

	// ③　建表语句
	public static final String TABLE = "contact";//
	public static final String DB = "contact.db";// 拼

	private class MyHelper extends SQLiteOpenHelper {

		public MyHelper(Context context) {
			super(context, DB, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "create table contact(_id integer auto  increment primary key ,account long ,nick text,avatar integer,sort text)";

			db.execSQL(sql);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}

	private MyHelper mMyHelper = null;

	// ④　获取OpenHelper实例
	// ⑤　获取数据库实例SqliteDatabase
	// ⑥　CRUD
	// ⑦　Junit

	private SQLiteDatabase db = null;

	@Override
	public boolean onCreate() {
		if (mMyHelper == null) {
			mMyHelper = new MyHelper(getContext());
		}
		return mMyHelper == null ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public static final Uri CONTACT_URI = Uri.parse("content://" + authority + "/" + TABLE);
	private static UriMatcher uris = null;
	private static final int CANTACT = 0;
	static {
		uris = new UriMatcher(UriMatcher.NO_MATCH);
		uris.addURI(authority, TABLE, CANTACT);
	}

	// content://authority/contact
	// content://authority/contact/1
	// ContentUris工具 1.uri +id 2. 获取id
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch (uris.match(uri)) {
		case CANTACT:
			db = mMyHelper.getWritableDatabase();
			long id = db.insert(TABLE, "", values);
			if (id != -1) {
				uri = ContentUris.withAppendedId(uri, id);
			}
			break;
		}
		return uri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
}
