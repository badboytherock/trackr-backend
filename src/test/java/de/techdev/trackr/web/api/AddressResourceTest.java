package de.techdev.trackr.web.api;

import de.techdev.trackr.domain.Address;
import de.techdev.trackr.domain.support.AddressDataOnDemand;
import de.techdev.trackr.web.MockMvcTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Moritz Schulze
 */
public class AddressResourceTest extends MockMvcTest {

    private final String addressJson = "{\"street\": \"street_1\", \"houseNumber\": \"1\", \"city\": \"city_1\", \"zipCode\": \"12345\", \"country\": \"country_1\"}";

    @Autowired
    private AddressDataOnDemand addressDataOnDemand;

    @Before
    public void setUp() throws Exception {
        addressDataOnDemand.init();
    }

    /**
     * Root is not accessible.
     * @throws Exception
     */
    @Test
    public void findAllNotExported() throws Exception {
        mockMvc.perform(
                get("/addresses")
                        .session(basicSession()))
               .andExpect(status().isMethodNotAllowed());
    }

    /**
     * One address is accessible.
     * @throws Exception
     */
    @Test
    public void one() throws Exception {
        Address address = addressDataOnDemand.getRandomObject();
        mockMvc.perform(
                get("/addresses/" + address.getId())
                        .session(basicSession()))
               .andExpect(status().isOk())
               .andExpect(content().contentType(standardContentType));
    }

    /**
     * An admin can create addresses.
     * @throws Exception
     */
    @Test
    public void createAllowedForAdmin() throws Exception {
        mockMvc.perform(
                post("/addresses")
                        .session(adminSession())
                        .content(addressJson))
               .andExpect(status().isCreated());
    }

    /**
     * An admin can edit addresses.
     * @throws Exception
     */
    @Test
    public void putAllowedForAdmin() throws Exception {
        Address address = addressDataOnDemand.getRandomObject();
        mockMvc.perform(
                put("/addresses/" + address.getId())
                        .session(adminSession())
                        .content(addressJson))
               .andExpect(status().isOk());
    }

    /**
     * An admin can edit addresses via PATCH.
     * @throws Exception
     */
    @Test
    public void patchAllowedForAdmin() throws Exception {
        Address address = addressDataOnDemand.getRandomObject();
        mockMvc.perform(
                patch("/addresses/" + address.getId())
                        .session(adminSession())
                        .content("{\"street\": \"test\"}"))
               .andExpect(status().isOk());
    }

    /**
     * A supervisor can not create addresses.
     * @throws Exception
     */
    @Test
    public void createNotAllowedForSupervisor() throws Exception {
        mockMvc.perform(
                post("/addresses")
                        .session(supervisorSession())
                        .content(addressJson))
               .andExpect(status().isForbidden());
    }

    /**
     * A supervisor can not edit addresses.
     * @throws Exception
     */
    @Test
    public void putForbiddenForSupervisor() throws Exception {
        Address address = addressDataOnDemand.getRandomObject();
        mockMvc.perform(
                put("/addresses/" + address.getId())
                        .session(supervisorSession())
                        .content(addressJson))
               .andExpect(status().isForbidden());
    }

    /**
     * A supervisor can not edit addresses via PATCH.
     * @throws Exception
     */
    @Test
    public void patchForbiddenForSupervisor() throws Exception {
        Address address = addressDataOnDemand.getRandomObject();
        mockMvc.perform(
                patch("/addresses/" + address.getId())
                        .session(supervisorSession())
                        .content("{\"street\": \"test\"}"))
               .andExpect(status().isForbidden());
    }

    /**
     * Addresses are not deletable.
     * @throws Exception
     */
    @Test
    public void deleteNotExported() throws Exception {
        Address address = addressDataOnDemand.getRandomObject();
        mockMvc.perform(
                delete("/addresses/" + address.getId())
                        .session(adminSession()))
               .andExpect(status().isMethodNotAllowed());
    }
}