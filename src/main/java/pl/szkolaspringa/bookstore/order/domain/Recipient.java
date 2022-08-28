package pl.szkolaspringa.bookstore.order.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Recipient {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String phone;

    private String street;

    private String city;

    private String zipCode;

    private String email;
}
