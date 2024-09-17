package api;

import lombok.Builder;

@Builder
public class Clients {
    private String name;
    private String town;
    private String address;
    private String bulstat;
    private Boolean is_reg_vat;
    private String vat_number;
    private String mol;
    private Boolean is_person;
    private String egn;
    private String country;
    private String code;
    private String office;
    private String name_en;
    private String town_en;
    private String address_en;
    private String mol_en;
    private String country_en;
}
