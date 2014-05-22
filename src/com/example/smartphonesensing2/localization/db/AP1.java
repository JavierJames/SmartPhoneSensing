package com.example.smartphonesensing2.localization.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class AP1 extends SQLiteOpenHelper {

	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "accelerometer.ap1";
    
    // query statements to create table
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    
    public static final String SQL_CREATE_TABLE =		
    	    "CREATE TABLE " + TestingField.TABLE_NAME + " (" +
    	    TestingField.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
    	    TestingField.FIELD_CELL + TEXT_TYPE + COMMA_SEP +
    	    TestingField.FIELD_0 + TEXT_TYPE + COMMA_SEP +
    	    TestingField.FIELD_1 + TEXT_TYPE +
    	    " );";
    
    // query statement to delete table
    private static final String SQL_DELETE_TABLE =
    	    "DROP TABLE IF EXISTS " + TestingField.TABLE_NAME;
	
	
	public AP1(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// 
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
	}
	
	
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
	
	
	
	/*Table 2 to store new records during testing*/
    public static abstract class TestingField implements BaseColumns {
        public static final String TABLE_NAME = "ap";
        public static final String FIELD_ID = "_id";
        public static final String FIELD_CELL = "cell";
        public static final String FIELD_0 = "rssi0";
        public static final String FIELD_1 = "rssi1";
        public static final String FIELD_2 = "rssi2";
        public static final String FIELD_3 = "rssi3";
        public static final String FIELD_4 = "rssi4";
        public static final String FIELD_5 = "rssi5";
        public static final String FIELD_6 = "rssi6";
        public static final String FIELD_7 = "rssi7";
        public static final String FIELD_8 = "rssi8";
        public static final String FIELD_9 = "rssi9";
        public static final String FIELD_10 = "rssi10";
        public static final String FIELD_11 = "rssi11";
        public static final String FIELD_12 = "rssi12";
        public static final String FIELD_13 = "rssi13";
        public static final String FIELD_14 = "rssi14";
        public static final String FIELD_15 = "rssi15";
        public static final String FIELD_16 = "rssi16";
        public static final String FIELD_17 = "rssi17";
        public static final String FIELD_18 = "rssi18";
        public static final String FIELD_19 = "rssi19";
        public static final String FIELD_20 = "rssi20";
        public static final String FIELD_21 = "rssi21";
        public static final String FIELD_22 = "rssi22";
        public static final String FIELD_23 = "rssi23";
        public static final String FIELD_24 = "rssi24";
        public static final String FIELD_25 = "rssi25";
        public static final String FIELD_26 = "rssi26";
        public static final String FIELD_27 = "rssi27";
        public static final String FIELD_28 = "rssi28";
        public static final String FIELD_29 = "rssi29";
        public static final String FIELD_30 = "rssi30";
        public static final String FIELD_31 = "rssi31";
        public static final String FIELD_32 = "rssi32";
        public static final String FIELD_33 = "rssi33";
        public static final String FIELD_34 = "rssi34";
        public static final String FIELD_35 = "rssi35";
        public static final String FIELD_36 = "rssi36";
        public static final String FIELD_37 = "rssi37";
        public static final String FIELD_38 = "rssi38";
        public static final String FIELD_39 = "rssi39";
        public static final String FIELD_40 = "rssi40";
        public static final String FIELD_41 = "rssi41";
        public static final String FIELD_42 = "rssi42";
        public static final String FIELD_43 = "rssi43";
        public static final String FIELD_44 = "rssi44";
        public static final String FIELD_45 = "rssi45";
        public static final String FIELD_46 = "rssi46";
        public static final String FIELD_47 = "rssi47";
        public static final String FIELD_48 = "rssi48";
        public static final String FIELD_49 = "rssi49";
        public static final String FIELD_50 = "rssi50";
        public static final String FIELD_51 = "rssi51";
        public static final String FIELD_52 = "rssi52";
        public static final String FIELD_53 = "rssi53";
        public static final String FIELD_54 = "rssi54";
        public static final String FIELD_55 = "rssi55";
        public static final String FIELD_56 = "rssi56";
        public static final String FIELD_57 = "rssi57";
        public static final String FIELD_58 = "rssi58";
        public static final String FIELD_59 = "rssi59";
        public static final String FIELD_60 = "rssi60";
        public static final String FIELD_71 = "rssi71";
        public static final String FIELD_72 = "rssi72";
        public static final String FIELD_73 = "rssi73";
        public static final String FIELD_74 = "rssi74";
        public static final String FIELD_75 = "rssi75";
        public static final String FIELD_76 = "rssi76";
        public static final String FIELD_77 = "rssi77";
        public static final String FIELD_78 = "rssi78";
        public static final String FIELD_79 = "rssi79";
        public static final String FIELD_80 = "rssi80";
        public static final String FIELD_81 = "rssi81";
        public static final String FIELD_83 = "rssi82";
        public static final String FIELD_84 = "rssi83";
        public static final String FIELD_85 = "rssi85";
        public static final String FIELD_86 = "rssi86";
        public static final String FIELD_87 = "rssi87";
        public static final String FIELD_88 = "rssi88";
        public static final String FIELD_89 = "rssi89";
        public static final String FIELD_90 = "rssi90";
        public static final String FIELD_91 = "rssi91";
        public static final String FIELD_92 = "rssi92";
        public static final String FIELD_93 = "rssi93";
        public static final String FIELD_94 = "rssi94";
        public static final String FIELD_95 = "rssi95";
        public static final String FIELD_96 = "rssi96";
        public static final String FIELD_97 = "rssi97";
        public static final String FIELD_98 = "rssi98";
        public static final String FIELD_99 = "rssi99";
        public static final String FIELD_100 = "rssi100";
        public static final String FIELD_101 = "rssi101";
        public static final String FIELD_102 = "rssi102";
        public static final String FIELD_103 = "rssi103";
        public static final String FIELD_104 = "rssi104";
        public static final String FIELD_105 = "rssi105";
        public static final String FIELD_106 = "rssi106";
        public static final String FIELD_107 = "rssi107";
        public static final String FIELD_108 = "rssi108";
        public static final String FIELD_109 = "rssi109";
        public static final String FIELD_110 = "rssi110";
        public static final String FIELD_111 = "rssi111";
        public static final String FIELD_112 = "rssi112";
        public static final String FIELD_113 = "rssi113";
        public static final String FIELD_114 = "rssi114";
        public static final String FIELD_115 = "rssi115";
        public static final String FIELD_116 = "rssi116";
        public static final String FIELD_117 = "rssi117";
        public static final String FIELD_118 = "rssi118";
        public static final String FIELD_119 = "rssi119";
        public static final String FIELD_120 = "rssi120";
        public static final String FIELD_121 = "rssi121";
        public static final String FIELD_122 = "rssi122";
        public static final String FIELD_123 = "rssi123";
        public static final String FIELD_124 = "rssi124";
        public static final String FIELD_125 = "rssi125";
        public static final String FIELD_126 = "rssi126";
        public static final String FIELD_127 = "rssi127";
        public static final String FIELD_128 = "rssi128";
        public static final String FIELD_129 = "rssi129";
        public static final String FIELD_130 = "rssi130";
        public static final String FIELD_131 = "rssi131";
        public static final String FIELD_132 = "rssi132";
        public static final String FIELD_133 = "rssi133";
        public static final String FIELD_134 = "rssi134";
        public static final String FIELD_135 = "rssi135";
        public static final String FIELD_136 = "rssi136";
        public static final String FIELD_137 = "rssi137";
        public static final String FIELD_138 = "rssi138";
        public static final String FIELD_139 = "rssi139";
        public static final String FIELD_140 = "rssi140";
        public static final String FIELD_141 = "rssi141";
        public static final String FIELD_142 = "rssi142";
        public static final String FIELD_143 = "rssi143";
        public static final String FIELD_144 = "rssi144";
        public static final String FIELD_145 = "rssi145";
        public static final String FIELD_146 = "rssi146";
        public static final String FIELD_147 = "rssi147";
        public static final String FIELD_148 = "rssi148";
        public static final String FIELD_149 = "rssi149";
        public static final String FIELD_150 = "rssi150";
        public static final String FIELD_151 = "rssi151";
        public static final String FIELD_152 = "rssi152";
        public static final String FIELD_153 = "rssi153";
        public static final String FIELD_154 = "rssi154";
        public static final String FIELD_155 = "rssi155";
        public static final String FIELD_156 = "rssi156";
        public static final String FIELD_157 = "rssi157";
        public static final String FIELD_158 = "rssi158";
        public static final String FIELD_159 = "rssi159";
        public static final String FIELD_160 = "rssi160";
        public static final String FIELD_161 = "rssi161";
        public static final String FIELD_162 = "rssi162";
        public static final String FIELD_163 = "rssi163";
        public static final String FIELD_164 = "rssi164";
        public static final String FIELD_165 = "rssi165";
        public static final String FIELD_166 = "rssi166";
        public static final String FIELD_167 = "rssi167";
        public static final String FIELD_168 = "rssi168";
        public static final String FIELD_169 = "rssi169";
        public static final String FIELD_170 = "rssi170";
        public static final String FIELD_171 = "rssi171";
        public static final String FIELD_172 = "rssi172";
        public static final String FIELD_173 = "rssi173";
        public static final String FIELD_174 = "rssi174";
        public static final String FIELD_175 = "rssi175";
        public static final String FIELD_176 = "rssi176";
        public static final String FIELD_177 = "rssi177";
        public static final String FIELD_178 = "rssi178";
        public static final String FIELD_179 = "rssi179";
        public static final String FIELD_180 = "rssi180";
        public static final String FIELD_191 = "rssi191";
        public static final String FIELD_192 = "rssi192";
        public static final String FIELD_193 = "rssi193";
        public static final String FIELD_194 = "rssi194";
        public static final String FIELD_195 = "rssi195";
        public static final String FIELD_196 = "rssi196";
        public static final String FIELD_197 = "rssi197";
        public static final String FIELD_198 = "rssi198";
        public static final String FIELD_199 = "rssi199";
        public static final String FIELD_200 = "rssi200";
        public static final String FIELD_201 = "rssi201";
        public static final String FIELD_202 = "rssi202";
        public static final String FIELD_203 = "rssi203";
        public static final String FIELD_204 = "rssi204";
        public static final String FIELD_205 = "rssi205";
        public static final String FIELD_206 = "rssi206";
        public static final String FIELD_207 = "rssi207";
        public static final String FIELD_208 = "rssi208";
        public static final String FIELD_209 = "rssi209";
        public static final String FIELD_210 = "rssi210";
        public static final String FIELD_211 = "rssi211";
        public static final String FIELD_213 = "rssi213";
        public static final String FIELD_212 = "rssi212";
        public static final String FIELD_214 = "rssi213";
        public static final String FIELD_215 = "rssi215";
        public static final String FIELD_216 = "rssi216";
        public static final String FIELD_217 = "rssi217";
        public static final String FIELD_218 = "rssi218";
        public static final String FIELD_219 = "rssi219";
        public static final String FIELD_220 = "rssi220";
        public static final String FIELD_221 = "rssi221";
        public static final String FIELD_222 = "rssi222";
        public static final String FIELD_223 = "rssi223";
        public static final String FIELD_224 = "rssi224";
        public static final String FIELD_225 = "rssi225";
        public static final String FIELD_226 = "rssi226";
        public static final String FIELD_227 = "rssi227";
        public static final String FIELD_228 = "rssi228";
        public static final String FIELD_229 = "rssi229";
        public static final String FIELD_230 = "rssi230";
        public static final String FIELD_231 = "rssi231";
        public static final String FIELD_232 = "rssi232";
        public static final String FIELD_233 = "rssi233";
        public static final String FIELD_234 = "rssi234";
        public static final String FIELD_235 = "rssi235";
        public static final String FIELD_236 = "rssi236";
        public static final String FIELD_237 = "rssi237";
        public static final String FIELD_238 = "rssi238";
        public static final String FIELD_239 = "rssi239";
        public static final String FIELD_240 = "rssi240";
        public static final String FIELD_241 = "rssi241";
        public static final String FIELD_242 = "rssi242";
        public static final String FIELD_243 = "rssi243";
        public static final String FIELD_244 = "rssi244";
        public static final String FIELD_245 = "rssi245";
        public static final String FIELD_246 = "rssi246";
        public static final String FIELD_247 = "rssi247";
        public static final String FIELD_248 = "rssi248";
        public static final String FIELD_249 = "rssi249";
        public static final String FIELD_250 = "rssi250";
        public static final String FIELD_251 = "rssi251";
        public static final String FIELD_252 = "rssi252";
        public static final String FIELD_253 = "rssi253";
        public static final String FIELD_254 = "rssi254";
        public static final String FIELD_255 = "rssi255";
    }

}
