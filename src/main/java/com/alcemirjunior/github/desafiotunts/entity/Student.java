package com.alcemirjunior.github.desafiotunts.entity;

import com.alcemirjunior.github.desafiotunts.SheetsQuickstart;
import com.google.api.services.sheets.v4.model.Sheet;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Student {

    private Long id;
    private String name;
    private Integer absencesClass;
    private Integer P1Score;
    private Integer P2Score;
    private Integer P3Score;
    private String situation;
    private Integer requiredScore;


    public Student(String id, String name, String absencesClass, String P1Score, String P2Score, String P3Score) {
        this.id = Long.parseLong(id);
        this.name = name;
        this.absencesClass = Integer.parseInt(absencesClass);
        this.P1Score = Integer.parseInt(P1Score);
        this.P2Score = Integer.parseInt(P2Score);
        this.P3Score = Integer.parseInt(P3Score);
    }

    public void setSituation(){
        if (absencesClass > 15){
            situation = "Reprovado por Falta";
        } else {
            Double media = (P1Score + P2Score + P3Score) / 3.0;
            if (media>=70) situation = "Aprovado";
            else if (media>=50) situation = "Exame Final";
            else situation = "Reprovado por Nota";
        }
    }

    public void setRequiredScore(){
        if (situation.equals("Exame Final")){
            double aux = (100.0 - (P1Score + P2Score + P3Score) / 3.0);
            requiredScore = (int)aux;
        }
        else requiredScore = 0;
    }
}