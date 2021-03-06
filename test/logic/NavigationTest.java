package logic;

import data.members.*;
import org.junit.Assert;
import org.junit.Test;
import java.util.Date;
import org.parse4j.ParseException;
import java.util.Set;
import java.util.HashSet;
import Exceptions.*;
import logic.Navigation;
import manager.logic.*;

public class NavigationTest {

	@Test
	public void getDistanceTest() {
		Assert.assertEquals(532, Navigation.getDistance((new MapLocation(32.777552, 35.020578)),
				(new MapLocation(32.778761, 35.016469)), false));
	}

	@Test
	public void getDurationTest() {
		Assert.assertEquals(82, Navigation.getDuration((new MapLocation(32.777552, 35.020578)),
				(new MapLocation(32.778761, 35.016469)), false));
	}

	@Test
	public void parkingSlotAtParkingAreaTest() {

		try {
			MapLocation location = new MapLocation(32.777408, 35.020332); // farest
			ParkingSlot taubSlot1 = new ParkingSlot("upperTaub-slot1", ParkingSlotStatus.FREE, StickersColor.RED,
					StickersColor.RED, location, new Date());
			location = new MapLocation(32.777223, 35.020890); // middle
			ParkingSlot taubSlot2 = new ParkingSlot("upperTaub-slot2", ParkingSlotStatus.FREE, StickersColor.RED,
					StickersColor.RED, location, new Date());
			location = new MapLocation(32.777195, 35.021281); // closest
			ParkingSlot taubSlot3 = new ParkingSlot("upperTaub-slot3", ParkingSlotStatus.FREE, StickersColor.RED,
					StickersColor.RED, location, new Date());

			Set<ParkingSlot> taubSlots = new HashSet<ParkingSlot>();
			taubSlots.add(taubSlot1);
			taubSlots.add(taubSlot2);
			taubSlots.add(taubSlot3);

			location = new MapLocation(32.777466, 35.021094);
			Destination destination = null;
			try{
				destination = new Destination("Taub-NavigationTest", location);
			} catch (AlreadyExists e){
				Assert.fail();
			}

			ParkingArea upperTaubArea = new ParkingArea(100, taubSlots, StickersColor.RED);
			try {

				User user = new User("3209654");
				ParkingSlot result = Navigation.parkingSlotAtParkingArea(user, upperTaubArea, destination);
				Assert.assertEquals(taubSlot3.getName(), result.getName());

				taubSlot3.changeStatus(ParkingSlotStatus.TAKEN);

				// now result 2 is the closest

				result = Navigation.parkingSlotAtParkingArea(user, upperTaubArea, destination);
				Assert.assertEquals(taubSlot2.getName(), result.getName());

				upperTaubArea.deleteParseObject();
				taubSlot1.deleteParseObject();
				taubSlot2.deleteParseObject();
				taubSlot3.deleteParseObject();
				destination.deleteParseObject();

			} catch (LoginException e) {
				System.out.println("login exception");
			}

		} catch (ParseException e) {
			System.out.print("parse exception");
		}
	}

	@Test
	public void closestParkingSlotTest() {

		try {

			// upper taub area + slots

			MapLocation location = new MapLocation(32.777408, 35.020332); // farest
			ParkingSlot taubSlot1 = new ParkingSlot("upperTaub-slot1", ParkingSlotStatus.FREE, StickersColor.RED,
					StickersColor.RED, location, new Date());
			location = new MapLocation(32.777223, 35.020890); // middle
			ParkingSlot taubSlot2 = new ParkingSlot("upperTaub-slot2", ParkingSlotStatus.FREE, StickersColor.RED,
					StickersColor.RED, location, new Date());
			location = new MapLocation(32.777195, 35.021281); // closest
			ParkingSlot taubSlot3 = new ParkingSlot("upperTaub-slot3", ParkingSlotStatus.FREE, StickersColor.RED,
					StickersColor.RED, location, new Date());

			Set<ParkingSlot> taubSlots = new HashSet<ParkingSlot>();
			taubSlots.add(taubSlot1);
			taubSlots.add(taubSlot2);
			taubSlots.add(taubSlot3);

			ParkingArea upperTaubArea = new ParkingArea(100, taubSlots, StickersColor.RED);

			// nesher area + slots

			location = new MapLocation(32.774596, 35.029031);
			ParkingSlot nesherSlot1 = new ParkingSlot("nesher-slot1", ParkingSlotStatus.FREE, StickersColor.WHITE,
					StickersColor.WHITE, location, new Date());

			Set<ParkingSlot> nesherSlots = new HashSet<ParkingSlot>();
			nesherSlots.add(nesherSlot1);

			ParkingArea nesherArea = new ParkingArea(101, nesherSlots, StickersColor.WHITE);

			// pool area + slots

			location = new MapLocation(32.778782, 35.016993); // farest
			ParkingSlot poolSlot1 = new ParkingSlot("pool-slot1", ParkingSlotStatus.FREE, StickersColor.BLUE,
					StickersColor.BLUE, location, new Date());
			location = new MapLocation(32.778818, 35.019418); // closest
			ParkingSlot poolSlot2 = new ParkingSlot("pool-slot2", ParkingSlotStatus.FREE, StickersColor.BLUE,
					StickersColor.BLUE, location, new Date());

			Set<ParkingSlot> poolSlots = new HashSet<ParkingSlot>();
			poolSlots.add(poolSlot1);
			poolSlots.add(poolSlot2);

			ParkingArea poolArea = new ParkingArea(102, poolSlots, StickersColor.BLUE);

			Set<ParkingArea> areas = new HashSet<ParkingArea>();
			areas.add(upperTaubArea);
			areas.add(nesherArea);
			areas.add(poolArea);

			ParkingAreas parkingAreas = new ParkingAreas(areas);

			location = new MapLocation(32.777466, 35.021094);
			Destination destination = null;
			try{
				destination = new Destination("Taub-NavigationTest", location);
			} catch (AlreadyExists e){
				Assert.fail();
			}

			try {

				User user = new User("3209654");
				// David's sticker type is red, lets change it to blue and will
				// return to original value at end
				user.setSticker(StickersColor.BLUE);

				ParkingSlot result = Navigation.closestParkingSlot(user, location, parkingAreas, destination);

				user.setSticker(StickersColor.RED);

				// taub slots are the closest but since the area is RED and
				// david's sticker is BLUE taub slots won't be checked
				Assert.assertEquals(poolSlot2.getName(), result.getName());

				upperTaubArea.deleteParseObject();
				taubSlot1.deleteParseObject();
				taubSlot2.deleteParseObject();
				taubSlot3.deleteParseObject();

				nesherArea.deleteParseObject();
				nesherSlot1.deleteParseObject();

				poolArea.deleteParseObject();
				poolSlot1.deleteParseObject();
				poolSlot2.deleteParseObject();
				
				destination.deleteParseObject();

			} catch (LoginException e) {
				System.out.println("login exception");
			}

		} catch (ParseException e) {
			System.out.print("parse exception");
		}

	}

	// the parkAtClosestSlot test is similar so I'll check only one parkAtArea
	@Test
	public void parkAtAreaTest() {
		boolean gotNoSlotAvailableException = false;
		try {

			MapLocation location = new MapLocation(32.777408, 35.020332);
			ParkingSlot taubSlot1 = new ParkingSlot("upperTaub-slot1", ParkingSlotStatus.FREE, StickersColor.RED,
					StickersColor.RED, location, new Date());

			Set<ParkingSlot> slots = new HashSet<ParkingSlot>();
			slots.add(taubSlot1);

			ParkingArea upperTaubArea = new ParkingArea(100, slots, StickersColor.RED);

			location = new MapLocation(32.777466, 35.021094);
			Destination destination = null;
			try{
				destination = new Destination("Taub-NavigationTest", location);
			} catch (AlreadyExists e){
				Assert.fail();
			}
			User user = null;
			try {
				user = new User("3209654");
				Navigation.parkAtArea(user, upperTaubArea, destination);
				Assert.assertEquals(user.getCurrentParking().getLocation().getLat(), taubSlot1.getLocation().getLat(), 0);
				Assert.assertEquals(user.getCurrentParking().getLocation().getLon(), taubSlot1.getLocation().getLon(), 0);
				Assert.assertEquals(user.getCurrentParking().getStatus().ordinal(), taubSlot1.getStatus().ordinal());
				Assert.assertEquals(user.getCurrentParking().getStatus(), ParkingSlotStatus.TAKEN);
				user.setCurrentParking(null);
				destination.deleteParseObject();
				upperTaubArea.deleteParseObject();
				taubSlot1.deleteParseObject();
				
			} catch (Exception e) {
				Assert.fail();
			}

			try {
				// the NoSlotAvailable exception should be thrown
				Navigation.parkAtArea(user, upperTaubArea, destination);
			} catch (NoSlotAvailable e) {
				gotNoSlotAvailableException = true;
				upperTaubArea.deleteParseObject();
				taubSlot1.deleteParseObject();
				destination.deleteParseObject();
			}

		} catch (ParseException e) {
			System.out.print("parse exception");
		}
		Assert.assertEquals(gotNoSlotAvailableException, true);
	}
}
