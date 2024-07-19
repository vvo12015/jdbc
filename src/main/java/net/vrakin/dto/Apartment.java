package net.vrakin.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Apartment {

    private Long apartmentId;

    private Region region;
    private String address;
    private Integer capacity;
    private Float price;

    public Apartment(Region region, String address, Integer capacity, Float price) {
        this.region = region;
        this.address = address;
        this.capacity = capacity;
        this.price = price;
    }

    public static final String TABLE_NAME = "APARTMENT";
    public static final String ID = "APARTMENT_ID";
    public static final String ADDRESS = "ADDRESS";
    public static final String CAPACITY = "CAPACITY";
    public static final String PRICE = "PRICE";
    public static final String REGION_REF = "REGION_REF";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + ID + " BIGINT NOT NULL AUTO_INCREMENT, "
            + ADDRESS + " TEXT, "  // Added comma here
            + CAPACITY + " INT NOT NULL, "
            + PRICE + " FLOAT, "
            + REGION_REF + " BIGINT NOT NULL, "
            + "PRIMARY KEY (" + ID + "),"
             + "FOREIGN KEY (" + REGION_REF + ") REFERENCES " + Region.TABLE_NAME + "(" + Region.ID + ")"
            + ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";\n";

    public static final String INSERT_APARTMENT = "INSERT INTO " + TABLE_NAME + "("
            + ADDRESS + ", "
            + CAPACITY + ", "
            + PRICE + ", "
            + REGION_REF
            + ") VALUES (?,?,?,?)";

    public static final String UPDATE_APARTMENT  = "UPDATE " + TABLE_NAME + " SET "
            + ADDRESS + " = ?, "
            + CAPACITY + " = ?, "
            + PRICE + " = ?, "
            + REGION_REF + " = ?" +
            " WHERE " + ID + " = ?";

    public static final String SELECT_APARTMENT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID + " = ?";

    public static final String SELECT_BY_ALL_PARAMETERS = "SELECT * FROM " + TABLE_NAME + " WHERE ";

    public static final String SELECT_APARTMENT_BY_ADDRESS =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + ADDRESS + " = ?";//+

    public static final String SELECT_APARTMENT_BY_REGION =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + REGION_REF + " = ?";//+

    public static final String SELECT_APARTMENT_BY_REGION_AND_ADDRESS =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + REGION_REF + " = ?" + " AND " + ADDRESS + " = ?";//+

    public static final String SELECT_APARTMENT_BY_REGION_AND_PRICE_BETWEEN =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + REGION_REF + " = ?" + " AND " + PRICE + " BETWEEN ? AND ?";//+

    public static final String SELECT_APARTMENT_BY_CAPACITY_BETWEEN_PRICE_BETWEEN =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + CAPACITY + " BETWEEN ? AND ?" + " AND " + PRICE + " BETWEEN ? AND ?";//+

    public static final String SELECT_APARTMENT_BY_REGION_CAPACITY_BETWEEN_PRICE_BETWEEN =
            "SELECT * FROM " + TABLE_NAME + " WHERE " + REGION_REF + " = ? AND " + CAPACITY + " BETWEEN ? AND ? " + "AND " + PRICE + " BETWEEN ? AND ?";//+

    public static final String SELECT_APARTMENT = "SELECT * FROM " + TABLE_NAME;

    public static final String DELETE_APARTMENT = "DELETE FROM " + TABLE_NAME + " WHERE " + ID + " = ?";
}
