package net.vrakin.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Region {
    private Long regionId;
    private String name;

    public Region(String name) {
        this.name = name;
    }

    public static final String TABLE_NAME = "REGION";
    public static final String ID = "REGION_ID";
    public static final String NAME = "NAME";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "( "
            + ID + " bigint PRIMARY KEY AUTO_INCREMENT, "
            + NAME + " TEXT)";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static final String INSERT_REGION = "INSERT INTO " + TABLE_NAME + " ( "
            + NAME + " )"
            + " VALUES (?)";

    public static final String SELECT_REGION_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID + " = ?";
    public static final String SELECT_REGION_BY_NAME =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + NAME + " = ?";
    public static final String SELECT_REGION = "SELECT * FROM " + TABLE_NAME;
    public static final String UPDATE_REGION  = "UPDATE " + TABLE_NAME + " SET "
            + NAME + " = ?, \n" +
            "WHERE " + ID + " = ?";
    public static final String DELETE_REGION = "DELETE FROM " + TABLE_NAME + " WHERE " + ID + " = ?";
}
