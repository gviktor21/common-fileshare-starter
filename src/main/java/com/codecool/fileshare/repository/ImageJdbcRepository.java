package com.codecool.fileshare.repository;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.UUID;


@Component("jdbc")
public class ImageJdbcRepository implements ImageRepository {

    /*
    *   // implement store image in database here
        // content is base64 coded image file
        // generate and return uuid of stored image
        // https://www.base64-image.de/
        // https://codebeautify.org/base64-to-image-converter
    *
    * */

    @Autowired
    private  Database db;

    @Override
    public String storeImage(String category, String content) {
        String uuid = null;

        if (insertNewImage(category,content)){
            uuid = getLastUUID(content);
        }
        return uuid;
    }

    @Override
    public String readImage(String uuid) {

        try (PreparedStatement stmt = getConnection().prepareStatement("select encode(content,'escape') as content from image WHERE id=?")) {
            stmt.setObject(1, UUID.fromString(uuid),java.sql.Types.OTHER);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getString("content");
            }else{
                System.out.println("No such a content");
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }


    private  Connection getConnection() {
        var ds = new PGSimpleDataSource();
        //ds.setURL(System.getenv("DB_URL"));
       // ds.setUser(System.getenv("DB_USER"));
        //ds.setPassword(System.getenv("DB_PASSWORD"));
        ds.setURL(db.getUrl());
        ds.setUser(db.getUsername());
        ds.setPassword(db.getPassword());
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private  boolean insertNewImage(String category, String content){
        String extension = content.split(";")[0].split("/")[1];
        System.out.println("The extension is"+extension);
        try (PreparedStatement stmt = getConnection().prepareStatement("INSERT INTO image(category,content,extension) VALUES(?,?,?)")) {
            stmt.setString(1,category);
            stmt.setBytes(2,content.getBytes());
            stmt.setString(3,extension);

            int numberOfRowsChanged = stmt.executeUpdate();
            if (numberOfRowsChanged==0){
                return false;
            }else {
                return true;
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return false;
    }
    private  String getLastUUID(String content){
            try (PreparedStatement stmt = getConnection().prepareStatement("select id from image WHERE content=?")) {
                stmt.setBytes(1,content.getBytes());
                ResultSet rs = stmt.executeQuery();
                if(rs.next()){
                    return rs.getString("id");
                }else{
                    System.out.println("No such a content");
                }

            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        return null;
    }
}
