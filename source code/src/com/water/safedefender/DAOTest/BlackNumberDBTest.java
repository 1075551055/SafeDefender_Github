package com.water.safedefender.DAOTest;

import java.util.List;
import java.util.Random;

import android.test.AndroidTestCase;

import com.water.safedefender.dao.BlackNumberDao;
import com.water.safedefender.db.BlackNumberListDBOpenHelper;
import com.water.safedefender.domain.BlackNumberInfo;

public class BlackNumberDBTest extends AndroidTestCase {
	public void testCreateDB(){
		BlackNumberListDBOpenHelper dbHelper = new BlackNumberListDBOpenHelper(getContext());
		dbHelper.getWritableDatabase();//调用这个API便会创建数据库
	}
	
	public void testAdd() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		long basenumber = 13500000000l;
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			dao.add(String.valueOf(basenumber+i), String.valueOf(random.nextInt(3)+1));
		}
	}
	
	public void testFindAll() throws Exception{
		BlackNumberDao dao = new BlackNumberDao(getContext());
		List<BlackNumberInfo> infos = dao.findAll();
		for(BlackNumberInfo info:infos){
			System.out.println(info.toString());
		}
	}

	public void testDelete() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.delete("110");
	}

	public void testUpdate() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.update("110", "2");
	}

	public void testFind() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		boolean result = dao.find("110");
		assertEquals(true, result);
	}
}
