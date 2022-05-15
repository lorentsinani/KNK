package repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//import com.mysql.cj.x.protobuf.MysqlxCrud;
import components.ErrorPopupComponent;
import components.SuccessPopupComponent;
import database.DBConnection;
import database.InsertQueryBuilder;
import database.UpdateQueryBuilder;
import helpers.DateHelper;
import helpers.Rooms;
import models.UserRole;

public class RoomRepository {

    static DBConnection connection = DBConnection.getConnection();


    public ArrayList<Rooms> findAll() throws Exception {
        String query = "select * from rooms";
//        ResultSet res = this.connection.executeQuery(query);
        ResultSet res = connection.executeQuery(query);
        ArrayList<Rooms> rooms = new ArrayList<>();
        while (res.next()) {
            System.out.println("Room: " + res.getString("room_number"));
            rooms.add(fromResultSet(res));
        }
        return rooms;
    }


    public static Rooms findAvailableRoom(Rooms room ) throws Exception {
        String query = "select * from rooms ro inner join reservations re on ro_room_number = re.room_id " +
                "where ro.room_number = " + room.getRoom_number() + " re.checkin_date is null and re.checkout_date is null";

        Statement stmt = connection.createStatement();
        ResultSet result = stmt.executeQuery(query);
        if(result.next()){
            return fromResultSet(result);
        }
        return null;
    }

    public static Rooms fromResultSet(ResultSet result) throws Exception {
        int id = result.getInt("room_number");
        int floor_number = result.getInt("floor_number");
        int capacity = result.getInt("capacity");
        int bed_number = result.getInt("bed_number");
        String room_type = result.getString("room_type");
        double price = result.getDouble("price");

        return new Rooms(id, floor_number, capacity, bed_number, room_type, price);
    }


    public static Rooms find(int id) throws Exception {
        String query = "select * from rooms where room_number = ?";
        PreparedStatement stmt = connection.prepareStatement(query);

        stmt.setInt(1, id);
        ResultSet result = stmt.executeQuery();

        if (!result.next()) {
            return null;
        }
        return fromResultSet(result);
    }

    public static Rooms update(Rooms rooms) throws Exception {
        UpdateQueryBuilder query = (UpdateQueryBuilder) UpdateQueryBuilder.create("rooms")
                .add("room_number", rooms.getRoom_number(), "i")
                .add("floor_number", rooms.getFloor_number(), "i")
                .add("capacity", rooms.getCapacity(), "i")
                .add("bed_number", rooms.getBed_number(), "i")
                .add("room_type", rooms.getRoom_type(), "s")
                .add("price", (float) rooms.getPrice(), "f");

        connection.execute(query);

        Rooms updatedRoom = find(rooms.getRoom_number());
        if (updatedRoom != null) {
            return updatedRoom;
        }
        ErrorPopupComponent.show("Room failed to be updated");
        return null;
    }


    public static Rooms create(Rooms rooms) throws Exception {
        try {
            InsertQueryBuilder query = (InsertQueryBuilder) InsertQueryBuilder.create("rooms")
                    .add("room_number", rooms.getRoom_number(), "i")
                    .add("floor_number", rooms.getFloor_number(), "i")
                    .add("capacity", rooms.getCapacity(), "i")
                    .add("bed_number", rooms.getBed_number(), "i")
                    .add("room_type", rooms.getRoom_type(), "s")
                    .add("price", (float) rooms.getPrice(), "f");

            int lastInsertedId = connection.execute(query);
            System.out.println("Last insertedid : " + lastInsertedId);
            Rooms room = find(lastInsertedId);

            if (room != null) {
                SuccessPopupComponent.show("Successfully created", "Register");
                return room;
            }
        } catch (Exception ex) {
            ErrorPopupComponent.show(ex);
        }
        return null;
    }

    public static ArrayList<Rooms> filterAvailableRooms(String checkIn, String checkOut, String roomType) throws Exception {

        String query;

        if (roomType.equals("All")) {
            query = "select * from rooms r where r.room_number not in(" +
                    "select r.room_number " +
                    "from reservations res inner join rooms r on res.room_id=r.room_number" +
                    "where (checkin_date between '" + checkIn + "' and '" + checkOut + "') and (checkout_date between '" + checkIn
                    + "' and '" + checkOut + "'))";
        } else {
            query = "select * from rooms r where r.room_type='" + roomType + "' and r.room_number not in(\n" +
                    "select r.room_number \n" +
                    "from reservations res inner join rooms r on res.room_id=r.room_number\n" +
                    "where (checkin_date between '" + checkIn + "' and '" + checkOut + "') and (checkout_date between '" + checkIn
                    + "' and '" + checkOut + "'))";
        }

        ArrayList<Rooms> rooms = new ArrayList<>();

        PreparedStatement stmt = connection.prepareStatement(query);
        ResultSet result = stmt.executeQuery();

        while (result.next()) {
            rooms.add(fromResultSet(result));
        }
        if (rooms != null) return rooms;

        return null;
    }


    public static ArrayList<Rooms> filterAllRooms(String type, String bedNumber, String capacity) throws Exception {
        StringBuilder query = new StringBuilder();
        boolean where = false;
        ArrayList<Rooms> rooms = new ArrayList<>();
        query.append("select * from rooms ");

        if (!type.equals("All")) {
            query.append("where room_type = '" + type + "' ");
            where = true;
        }
        if (!bedNumber.equals("All")) {
            query.append(where ? "and" : "where");
            query.append("bedNumber = " + Integer.parseInt(bedNumber) + " ");
            where = true;
        }
        if (!capacity.equals("All")) {
            query.append(where ? "and" : "where");
            query.append("capactiy = " + Integer.parseInt(capacity) + "");
        }

<<<<<<< Updated upstream
     public ResultSet getAvailableRooms(String checkin, String checkout, String type) throws Exception {
        DBConnection connection = DBConnection.getConnection();
        String query;
        if (type == "All") {
        	query = "select * from rooms r where r.room_number not in(\n" +
             "select r.room_number \n" +
             "from reservations res inner join rooms r on res.room_id=r.room_number\n" +
             "where (checkin_date between '" + checkin + "' and '" + checkout + "') and (checkout_date between '" + checkin
             + "' and '" + checkout + "'))";
       } else {
    	   query = "select * from rooms r where r.room_type='" + type + "' and r.room_number not in(\n" +
             "select r.room_number \n" +
             "from reservations res inner join rooms r on res.room_id=r.room_number\n" +
             "where (checkin_date between '" + checkin + "' and '" + checkout + "') and (checkout_date between '" + checkin
             + "' and '" + checkout + "'))";
       }

       Statement stmt = connection.createStatement();
       ResultSet rs = stmt.executeQuery(query);

       return rs;
     }
=======
        PreparedStatement stmt = connection.prepareStatement(query.toString());
        ResultSet result = stmt.executeQuery();
        while (result.next()) {
            rooms.add(fromResultSet(result));
        }
        if (rooms != null) return rooms;
        return null;
    }


>>>>>>> Stashed changes

    // public static List<RoomChartModel> selectAllGroupByRoomType() throws Exception {
    //   ArrayList<RoomChartModel> list = new ArrayList<>();
    //   Connection conn = DBConnect.getConnection();
    //   PreparedStatement stmt = conn.prepareStatement("SELECT room_type, COUNT(*) FROM rooms GROUP BY room_type ");
    //   ResultSet res = stmt.executeQuery();
    //   while (res.next()) {
    //     list.add(new RoomChartModel(res.getString("room_type"), res.getInt("COUNT(*)")));
    //   }
    //   return list;
    // }


    // public static List<RoomChartModel> selectAllGroupByFloorNum() throws Exception {
    //   ArrayList<RoomChartModel> list = new ArrayList<>();
    //   Connection conn = DBConnect.getConnection();
    //   PreparedStatement stmt = conn.prepareStatement("SELECT rr.floor_number, COUNT(*) FROM reservations r \n" +
    //       "INNER JOIN rooms rr ON r.room_id = rr.room_number\n" +
    //       "GROUP BY rr.floor_number ");
    //   ResultSet res = stmt.executeQuery();
    //   while (res.next()) {
    //     list.add(new RoomChartModel(res.getInt("floor_number"), res.getInt("COUNT(*)")));
    //   }
    //   return list;
    // }


}
