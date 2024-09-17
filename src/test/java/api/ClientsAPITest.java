package api;

import io.restassured.response.Response;
import lombok.experimental.FieldDefaults;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import sun.security.krb5.internal.crypto.RsaMd5CksumType;

public class ClientsAPITest {

    private String token;
    ClientsAPI clientsAPI = new ClientsAPI();

    @Before
    public void setToken(){
        Login login = new Login();
        token = login.getToken();
    }
    @Test
    public void canGetListWithClients(){
        Response getResponse = clientsAPI.getAllClients(token);
        Assertions.assertEquals(200, getResponse.statusCode());
    }
    @Test
    public void canCreateAClientWithRequiredFields(){
        Clients client1 = new Clients.ClientsBuilder()
                .name("Mihail Petrov")
                .town("Gabrovo")
                .address("Centar").build();
        Response createResponse = clientsAPI.createClient(token, client1);
        Assertions.assertEquals(201, createResponse.statusCode());
        Response getResponse = clientsAPI.getSingleClient(token, createResponse.jsonPath().getInt("id"));
        Assertions.assertEquals("Mihail Petrov", getResponse.jsonPath().getString("name"));
        Assertions.assertEquals("Gabrovo", getResponse.jsonPath().getString("town"));
        Assertions.assertEquals("Centar", getResponse.jsonPath().getString("address"));
    }

    @Test
    public void canCreateAClientAsCompany(){
        Clients client1 = new Clients.ClientsBuilder()
                .name("Radoslav Petrov")
                .town("Gabrovo")
                .address("Centar")
                .is_person(false)
                .bulstat("345")
                .mol("Ivailo Petrov")
                .is_reg_vat(true)
                .vat_number("9876").build();
        Response createResponse = clientsAPI.createClient(token, client1);
        Assertions.assertEquals(201, createResponse.statusCode());
        Response getResponse = clientsAPI.getSingleClient(token, createResponse.jsonPath().getInt("id"));
        Assertions.assertEquals("Radoslav Petrov", getResponse.jsonPath().getString("name"));
        Assertions.assertEquals("Gabrovo", getResponse.jsonPath().getString("town"));
        Assertions.assertEquals("Centar", getResponse.jsonPath().getString("address"));
        Assertions.assertEquals(false, getResponse.jsonPath().getBoolean("is_person"));
        Assertions.assertEquals("345", getResponse.jsonPath().getString("bulstat"));
        Assertions.assertEquals("Ivailo Petrov", getResponse.jsonPath().getString("mol"));
        Assertions.assertEquals(true, getResponse.jsonPath().getBoolean("is_reg_vat"));
        Assertions.assertEquals("9876", getResponse.jsonPath().getString("vat_number"));
    }

    @Test
    public void canCreateAClientAsCompanyWithoutVat(){
        Clients client1 = new Clients.ClientsBuilder()
                .name("Aq Peneva")
                .town("Gabrovo")
                .address("Centar")
                .is_person(false)
                .bulstat("654")
                .mol("Ivailo Petrov")
                .is_reg_vat(false).build();
        Response createResponse = clientsAPI.createClient(token, client1);
        Assertions.assertEquals(201, createResponse.statusCode());
        Response getResponse = clientsAPI.getSingleClient(token, createResponse.jsonPath().getInt("id"));
        Assertions.assertEquals("Aq Peneva", getResponse.jsonPath().getString("name"));
        Assertions.assertEquals("Gabrovo", getResponse.jsonPath().getString("town"));
        Assertions.assertEquals("Centar", getResponse.jsonPath().getString("address"));
        Assertions.assertEquals(false, getResponse.jsonPath().getBoolean("is_person"));
        Assertions.assertEquals("654", getResponse.jsonPath().getString("bulstat"));
        Assertions.assertEquals("Ivailo Petrov", getResponse.jsonPath().getString("mol"));
        Assertions.assertEquals(false, getResponse.jsonPath().getBoolean("is_reg_vat"));
    }
    @Test
    public void canCreateClientAsPerson(){
        Clients client = new Clients.ClientsBuilder()
                .name("Naq Ivanova")
                .town("Varna")
                .address("Primorski blvd.")
                .is_person(true)
                .egn("764").build();
        Response createResponse = clientsAPI.createClient(token, client);
        Assertions.assertEquals(201, createResponse.statusCode());
        int id = createResponse.jsonPath().getInt("id");
        Response getResponse = clientsAPI.getSingleClient(token, id);
        Assertions.assertEquals(200, getResponse.statusCode());
        Assertions.assertEquals("Naq Ivanova", getResponse.jsonPath().getString("name"));
        Assertions.assertEquals("Varna", getResponse.jsonPath().getString("town"));
        Assertions.assertEquals("Primorski blvd.", getResponse.jsonPath().getString("address"));
        Assertions.assertEquals(true, getResponse.jsonPath().getBoolean("is_person"));
        Assertions.assertEquals("764", getResponse.jsonPath().getString("egn"));
    }
    @Test
    public void cantCreateSameClient(){
        Clients client1 = new Clients.ClientsBuilder()
                .name("Jordan Ivanov")
                .town("Varna")
                .address("San Stefano str.")
                .is_person(false)
                .bulstat("002")
                .mol("Ivailo Petrov")
                .is_reg_vat(false).build();
        Response createResponse = clientsAPI.createClient(token, client1);
        Assertions.assertEquals(201, createResponse.statusCode());
        Response createSecondResponse = clientsAPI.createClient(token, client1);
        Assertions.assertEquals(400, createSecondResponse.statusCode());
    }
    @Test
    public void cantCreateClientWithSameNameAndEGN(){
        Clients client1 = new Clients.ClientsBuilder()
                .name("Mihail Stoianov")
                .town("Varna")
                .address("San Stefano str.")
                .is_person(true)
                .egn("8901190876").build();
        Response createResponse = clientsAPI.createClient(token, client1);
        Assertions.assertEquals(201, createResponse.statusCode());
        Clients client2 = new Clients.ClientsBuilder()
                .name("Mihail Stoianov")
                .town("Gabrovo")
                .address("Racho Kovacha str.")
                .is_person(true)
                .egn("8901190876").build();
        Response createSecondClientResponse = clientsAPI.createClient(token, client2);
        Assertions.assertEquals(201, createSecondClientResponse.statusCode());
        Response getResponse = clientsAPI.getSingleClient(token, createSecondClientResponse.jsonPath().getInt("id"));
        Assertions.assertEquals(200, getResponse.statusCode());
        Assertions.assertEquals("Mihail Stoianov", getResponse.jsonPath().getString("name"));
        Assertions.assertEquals("Gabrovo", getResponse.jsonPath().getString("town"));
    }
    @Test
    public void canCreateClientWithSameName(){
        Clients client = new Clients.ClientsBuilder()
                .name("Zori Ivanova")
                .town("Varna")
                .address("Primorski blvd.")
                .is_person(true)
                .egn("9876").build();
        Response createFirstResponse = clientsAPI.createClient(token, client);
        Assertions.assertEquals(201, createFirstResponse.statusCode());
        Response getFirstResponse = clientsAPI.getSingleClient(token, createFirstResponse.jsonPath().getInt("id"));
        Assertions.assertEquals("Zori Ivanova", getFirstResponse.jsonPath().getString("name"));
        Assertions.assertEquals("Varna", getFirstResponse.jsonPath().getString("town"));
        Assertions.assertEquals("Primorski blvd.", getFirstResponse.jsonPath().getString("address"));
        Assertions.assertEquals(true, getFirstResponse.jsonPath().getBoolean("is_person"));
        Assertions.assertEquals("9876", getFirstResponse.jsonPath().getString("egn"));
        Clients client1 = new Clients.ClientsBuilder()
                .name("Zori Ivanova")
                .town("Burgas")
                .address("Chaika str.")
                .is_person(true)
                .egn("6543").build();
        Response createSecondResponse = clientsAPI.createClient(token, client1);
        Assertions.assertEquals(201, createSecondResponse.statusCode());
        Response getSecondResponse = clientsAPI.getSingleClient(token, createSecondResponse.jsonPath().getInt("id"));
        Assertions.assertEquals("Zori Ivanova", getSecondResponse.jsonPath().getString("name"));
        Assertions.assertEquals("Burgas", getSecondResponse.jsonPath().getString("town"));
        Assertions.assertEquals("Chaika str.", getSecondResponse.jsonPath().getString("address"));
        Assertions.assertEquals(true, getSecondResponse.jsonPath().getBoolean("is_person"));
        Assertions.assertEquals("6543", getSecondResponse.jsonPath().getString("egn"));
    }
    @Test
    public void cantCreateClientWithSameBulstat(){
        Clients client1 = new Clients.ClientsBuilder()
                .name("Ivan Ivanov")
                .town("Varna")
                .address("San Stefano str.")
                .is_person(false)
                .bulstat("001")
                .mol("Ivailo Petrov")
                .is_reg_vat(false).build();
        Response createResponse = clientsAPI.createClient(token, client1);
        Assertions.assertEquals(201, createResponse.statusCode());
        Clients client2 = new Clients.ClientsBuilder()
                .name("Ivan Petrov")
                .town("Kavarna")
                .address("Morska str.")
                .is_person(false)
                .bulstat("001")
                .mol("Jordan Petrov")
                .is_reg_vat(false).build();
        Response createSecondClientResponse = clientsAPI.createClient(token, client2);
        Assertions.assertEquals(400,createSecondClientResponse.statusCode());
    }
    @Test
    public void cantCreateClientWithSameEGN(){
        Clients client1 = new Clients.ClientsBuilder()
                .name("Rado Ivanov")
                .town("Varna")
                .address("San Stefano str.")
                .is_person(true)
                .egn("8910223499").build();
        Response createResponse = clientsAPI.createClient(token, client1);
        Assertions.assertEquals(201, createResponse.statusCode());
        Response getResponse = clientsAPI.getSingleClient(token, createResponse.jsonPath().getInt("id"));
        Assertions.assertEquals("Rado Ivanov", getResponse.jsonPath().getString("name"));
        Assertions.assertEquals("8910223499", getResponse.jsonPath().getString("egn"));
        Clients client2 = new Clients.ClientsBuilder()
                .name("Mihaela Ivanova")
                .town("Varna")
                .address("San Stefano str.")
                .is_person(true)
                .egn("8910223499").build();
        Response createSecondResponse = clientsAPI.createClient(token, client2);
        Response getSecondResponse = clientsAPI.getSingleClient(token, createSecondResponse.jsonPath().getInt("id"));
        Assertions.assertEquals("Mihaela Ivanova", getSecondResponse.jsonPath().getString("name"));
        Assertions.assertEquals("8910223499", getSecondResponse.jsonPath().getString("egn"));
        Assertions.assertEquals(400, createResponse.statusCode());
    }
    @Test
    public void cantCreateClientWithSameVAT(){
        Clients client1 = new Clients.ClientsBuilder()
                .name("Stela Petrova")
                .town("Gabrovo")
                .address("Centar")
                .is_person(false)
                .bulstat("523")
                .mol("Ivailo Petrov")
                .is_reg_vat(true)
                .vat_number("98760").build();
        Response createResponse = clientsAPI.createClient(token, client1);
        Assertions.assertEquals(201, createResponse.statusCode());
        Response getFirstResponse = clientsAPI.getSingleClient(token, createResponse.jsonPath().getInt("id"));
        Assertions.assertEquals("Stela Petrova", getFirstResponse.jsonPath().getString("name"));
        Assertions.assertEquals("98760", getFirstResponse.jsonPath().getString("vat_number"));
        Clients client2 = new Clients.ClientsBuilder()
                .name("Ema Petrova")
                .town("Sevlievo")
                .address("Centar")
                .is_person(false)
                .bulstat("5234")
                .mol("Ivailo Petrov")
                .is_reg_vat(true)
                .vat_number("98760").build();
        Response createSecondResponse = clientsAPI.createClient(token, client2);
        Assertions.assertEquals(400, createSecondResponse.statusCode());
    }
    @Test
    public void canCreateClientWithAllFields(){
        Clients client = new Clients.ClientsBuilder()
                .name("Яна Симеонова")
                .town("Севлиево")
                .address("Център")
                .is_person(false)
                .bulstat("34567890")
                .mol("Ивайло Петров")
                .is_reg_vat(true)
                .vat_number("44444456")
                .country("БГ")
                .code("6660")
                .office("Пазара")
                .mol_en("Ivailo Petrov")
                .name_en("Emilia Petrova")
                .address_en("Centar")
                .country_en("BG")
                .town_en("Sevlievo").build();
        Response createResponse = clientsAPI.createClient(token, client);
        Assertions.assertEquals(201, createResponse.statusCode());
        Response getResponse = clientsAPI.getSingleClient(token, createResponse.jsonPath().getInt("id"));
        Assertions.assertEquals("Яна Симеонова", getResponse.jsonPath().getString("name"));
        Assertions.assertEquals("Севлиево", getResponse.jsonPath().getString("town"));
        Assertions.assertEquals("Център", getResponse.jsonPath().getString("address"));
        Assertions.assertEquals("34567890", getResponse.jsonPath().getString("bulstat"));
        Assertions.assertEquals("Ивайло Петров", getResponse.jsonPath().getString("mol"));
        Assertions.assertEquals("44444456", getResponse.jsonPath().getString("vat_number"));
        Assertions.assertEquals("БГ", getResponse.jsonPath().getString("country"));
        Assertions.assertEquals("6660", getResponse.jsonPath().getString("code"));
        Assertions.assertEquals("Пазара", getResponse.jsonPath().getString("office"));
        Assertions.assertEquals("Ivailo Petrov", getResponse.jsonPath().getString("mol_en"));
        Assertions.assertEquals("Emilia Petrova", getResponse.jsonPath().getString("name_en"));
        Assertions.assertEquals("Centar", getResponse.jsonPath().getString("address_en"));
        Assertions.assertEquals("BG", getResponse.jsonPath().getString("country_en"));
        Assertions.assertEquals("Sevlievo", getResponse.jsonPath().getString("town_en"));
    }
    @Test
    public void canRestoreClient(){
        Clients client = new Clients.ClientsBuilder()
                .name("Katq Tomova")
                .address("Aqzmo")
                .town("Stara Zagora")
                .is_person(true)
                .egn("9310228765").build();
        Response createResponse = clientsAPI.createClient(token, client);
        Assertions.assertEquals(201, createResponse.statusCode());
        Response deleteResponse = clientsAPI.deleteClient(token, createResponse.jsonPath().getInt("id"));
        Assertions.assertEquals(204, deleteResponse.statusCode());
        Response getResponse = clientsAPI.getSingleClient(token, createResponse.jsonPath().getInt("id"));
        Assertions.assertEquals(404, getResponse.statusCode());
        Clients client1 = new Clients.ClientsBuilder()
                .name("Katq Tomova")
                .address("Centar")
                .town("Kazanlyk")
                .is_person(true)
                .egn("9310228765").build();
        Response createClient1Response = clientsAPI.createClient(token, client1);
        Response getClient1Response = clientsAPI.getSingleClient(token, createClient1Response.jsonPath().getInt("id"));
        Assertions.assertEquals(404, getClient1Response.statusCode());
        Response restoreResponse = clientsAPI.restoreClient(token, createClient1Response.jsonPath().getInt("id"));
        Assertions.assertEquals(204, restoreResponse.statusCode());
        Response getRestoredClientResponse = clientsAPI.getSingleClient(token, createResponse.jsonPath().getInt("id"));
        Assertions.assertEquals("Katq Tomova", getRestoredClientResponse.jsonPath().getString("name"));
        Assertions.assertEquals("Aqzmo", getRestoredClientResponse.jsonPath().getString("address"));
        Assertions.assertEquals("Stara Zagora", getRestoredClientResponse.jsonPath().getString("town"));
        Assertions.assertEquals("9310228765", getRestoredClientResponse.jsonPath().getString("egn"));
    }
    @Test
    public void canDeleteClient(){
        //have a client created
        Clients clients = new Clients.ClientsBuilder()
                .name("Ivailo Petrov")
                .address("Primorski blvd.")
                .town("Varna")
                .is_person(true)
                .egn("12345").build();
        Response createResponse = clientsAPI.createClient(token, clients);
        Assertions.assertEquals(201, createResponse.statusCode());
        //delete the client
        Response deleteResponse = clientsAPI.deleteClient(token, createResponse.jsonPath().getInt("id"));
        Assertions.assertEquals(204, deleteResponse.statusCode());
        Response getResponse = clientsAPI.getSingleClient(token, createResponse.jsonPath().getInt("id"));
        Assertions.assertEquals(404, getResponse.statusCode());
    }
}
