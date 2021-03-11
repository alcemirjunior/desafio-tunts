package com.alcemirjunior.github.desafiotunts;

import com.alcemirjunior.github.desafiotunts.entity.Student;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SheetsQuickstart {
    private static final String APPLICATION_NAME = "desafio tunts";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = SheetsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("493104512215-o02nvkct1iv8tflrkt722ce16on3pog4.apps.googleusercontent.com");
    }

    /**
     * Prints the names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     */
    public static void main(String... args) throws IOException, GeneralSecurityException {
        List<Student> students = new ArrayList<>();
        fromSheetToStudentClass(students);
        System.out.println("\n");
        System.out.println("------------------ A lista de Alunos será atualizada nos campos situaion e requiredScore:-----------------------");
        System.out.println("\n");
        students.stream().forEach(student -> student.setSituation());
        students.stream().forEach(student -> student.setRequiredScore());
        students.stream().forEach(student -> System.out.println(student));
        fromStudentUpdateToSheet(students);

    }
    public static void fromSheetToStudentClass (List<Student> students) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "1rsBjE0kx2iHTnzcr4Dt7T8fW9M_RqcK_1qmddFrm2T4";
        final String range = "engenharia_de_software!A4:F27";
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            System.out.println("\n");
            System.out.println("-----------------Os dados da Planilha serão copiados para uma lista de estudantes:--------------------------");
            System.out.println("\n");
            for (List row : values) {
                Student student = new Student(
                        row.get(0).toString(),
                        row.get(1).toString(),
                        row.get(2).toString(),
                        row.get(3).toString(),
                        row.get(4).toString(),
                        row.get(5).toString());
                students.add(student);
                System.out.println(student);
            }
        }
    }

    public static void fromStudentUpdateToSheet(List<Student> students) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "1rsBjE0kx2iHTnzcr4Dt7T8fW9M_RqcK_1qmddFrm2T4";
        final String range = "engenharia_de_software!G4:H27";
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        String situation;
        String requiredScore;
        String position;
        System.out.println("\n");
        System.out.println("-----------------------Os campos situation e requiredScore serão atualizados na planilha--------------------------");
        System.out.println("\n");
        for (Integer i=1; i<=24; i++){
            situation = students.get(i-1).getSituation();
            requiredScore = students.get(i-1).getRequiredScore().toString();
            Integer j = i+3;
            position = "G".concat(j.toString());

            ValueRange body = new ValueRange()
                    .setValues(Arrays.asList(
                            Arrays.asList(situation, requiredScore)
                    ));
            UpdateValuesResponse result = service.spreadsheets().values()
                    .update(spreadsheetId, position, body)
                    .setValueInputOption("RAW")
                    .execute();
            System.out.println(students.get(i-1));
        }
    }
}
