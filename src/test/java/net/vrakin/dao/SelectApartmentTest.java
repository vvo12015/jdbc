package net.vrakin.dao;

import lombok.extern.slf4j.Slf4j;
import net.vrakin.dto.Apartment;
import net.vrakin.dto.Region;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class SelectApartmentTest {

    private final static ApartmentDao apartmentDao = new ApartmentDao();
    private final static RegionDao regionDao = new RegionDao();
    public static final int START_TEST_NUMBER = 1;
    public static final int END_TEST_NUMBER = 50;
    private static final int THREE_NUMBER = 3;
    private static final int TWO_NUMBER = 2;

    @Before
    public void setUp() throws Exception {
        apartmentDao.createTable();

        List<Apartment> apartments = new ArrayList<>();

        IntStream.range(START_TEST_NUMBER, THREE_NUMBER)
                .forEach(i -> {apartments.add(new Apartment(new Region("region" + THREE_NUMBER),"address" + THREE_NUMBER,
                        THREE_NUMBER, THREE_NUMBER*1000.00F));});

        IntStream.range(START_TEST_NUMBER, TWO_NUMBER)
                .forEach(i -> {apartments.add(new Apartment(new Region("region" + TWO_NUMBER),"address" + TWO_NUMBER,
                        TWO_NUMBER, TWO_NUMBER*1000.00F));});


    }

    @Test
    public void findByAddress() {
        /*Apartment apt = getApartment();

        Apartment new_apt = apartmentDao.add(apt);

        Assert.assertNotNull(new_apt);
        Assert.assertEquals(apt.getAddress(), new_apt.getAddress());
        Assert.assertEquals(apt.getPrice(), new_apt.getPrice());
        Assert.assertEquals(apt.getCapacity(), new_apt.getCapacity());
        Assert.assertEquals(apt.getRegion().getRegionId().longValue(), new_apt.getRegion().getRegionId().longValue());
        Assert.assertEquals(apt.getRegion().getName(), new_apt.getRegion().getName());
         */
    }
}
