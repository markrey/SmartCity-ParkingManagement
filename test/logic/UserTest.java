package logic;

import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.parse4j.ParseException;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;
import Exceptions.LoginException;
import data.members.*;

/**
 * @Author DavidCohen55
 */
public class UserTest {

	/*
	 * test that checks that if you update a User parking slot from the user
	 * itself it will change it also in the parse server
	 */
	@Test
	public void test01() {
		User user = null;
		try {
			user = new User("3209654");
			if (user != null)
				user.setCurrentParking(new ParkingSlot("DavidSlot", ParkingSlotStatus.FREE, StickersColor.RED,
						StickersColor.RED, new MapLocation(32.778153, 35.021855), new Date()));
			Assert.assertEquals(user.getCurrentParking().getName(), "DavidSlot");
			user.getCurrentParking().setColor(StickersColor.BORDEAUX);
			ParseQuery<ParseObject> query = ParseQuery.getQuery("ParkingSlot");
			Assert.assertEquals(new ParkingSlot(user.getCurrentParking().getParseObject()).getColor(),
					StickersColor.BORDEAUX);
			ParseObject park = query.get((user.getCurrentParking().getParseObject()).getObjectId());
			Assert.assertEquals(StickersColor.values()[park.getInt("color")], StickersColor.BORDEAUX);
			user.setCurrentParking(null);
			park.delete();
		} catch (LoginException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (ParseException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/*
	 * test that checks that if you delete the parking slot from the parse
	 * server it will change it also in the user parking slot to null
	 */
	@Test
	public void test02() {
		try {
			User user = new User("3209654");
			ParkingSlot ps = new ParkingSlot("DavidSlot2", ParkingSlotStatus.FREE, StickersColor.RED, StickersColor.RED,
					new MapLocation(32.778153, 35.021855), new Date());
			if (user != null)
				user.setCurrentParking(ps);
			Assert.assertEquals(user.getCurrentParking().getName(), "DavidSlot2");
			ps.deleteParseObject();
			Assert.assertEquals(user.getCurrentParking(), null);
		} catch (LoginException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (ParseException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/*
	 * test that checks that if you update a User parking slot from parse server
	 * it will change it also in the user itself
	 */
	@Test
	public void test03() {
		try {
			User user = new User("3209654");
			if (user != null)
				user.setCurrentParking(new ParkingSlot("DavidSlot3", ParkingSlotStatus.FREE, StickersColor.RED,
						StickersColor.RED, new MapLocation(32.778153, 35.021855), new Date()));
			Assert.assertEquals(user.getCurrentParking().getName(), "DavidSlot3");
			ParseQuery<ParseObject> query = ParseQuery.getQuery("ParkingSlot");
			ParseObject park = query.get((user.getCurrentParking().getParseObject()).getObjectId());
			Assert.assertEquals(StickersColor.values()[park.getInt("color")], StickersColor.RED);
			Assert.assertEquals(park.getString("name"), "DavidSlot3");
			park.put("name", "DavidSlot4");
			park.save();
			Assert.assertEquals(park.getString("name"), "DavidSlot4");
			Assert.assertEquals(user.getCurrentParking().getName(), "DavidSlot4");
			Assert.assertEquals(new ParkingSlot(user.getCurrentParking().getParseObject()).getName(), "DavidSlot4");
			user.setCurrentParking(null);
			park.delete();
		} catch (ParseException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (LoginException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/*
	 * test that checks that if you give a User a different parking slot it will
	 * change it
	 */
	@Test
	public void test04() {
		try {
			User user = new User("3209654");
			if (user != null)
				user.setCurrentParking(new ParkingSlot("DavidSlot3", ParkingSlotStatus.FREE, StickersColor.RED,
						StickersColor.RED, new MapLocation(32.778153, 35.021855), new Date()));
			ParseObject park1 = user.getCurrentParking().getParseObject();
			Assert.assertEquals(user.getCurrentParking().getName(), "DavidSlot3");
			Assert.assertEquals(user.getCurrentParking().getColor(), StickersColor.RED);
			Assert.assertEquals(user.getCurrentParking().getStatus(), ParkingSlotStatus.FREE);
			if (user != null)
				user.setCurrentParking(new ParkingSlot("DavidSlot4", ParkingSlotStatus.TAKEN, StickersColor.GREEN,
						StickersColor.GREEN, new MapLocation(32.778153, 35.021855), new Date()));
			ParseObject park2 = user.getCurrentParking().getParseObject();
			Assert.assertEquals(user.getCurrentParking().getName(), "DavidSlot4");
			Assert.assertEquals(user.getCurrentParking().getColor(), StickersColor.GREEN);
			Assert.assertEquals(user.getCurrentParking().getStatus(), ParkingSlotStatus.TAKEN);
			user.setCurrentParking(null);
			park1.delete();
			park2.delete();
		} catch (LoginException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (ParseException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
