package com.project.importexel.entites;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Soins {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private  Long id ;
    private  String Region;
    private  String Delegation;
    private  String Commune ;
    private  String Nom_établissement ;
    private  String Categorie ;

}
