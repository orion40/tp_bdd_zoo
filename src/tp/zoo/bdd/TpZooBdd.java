/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tp.zoo.bdd;

import java.sql.*;
import static java.sql.Connection.*;
import java.util.ArrayList;

public class TpZooBdd {

    static final String CONN_URL = "jdbc:oracle:thin:@im2ag-oracle.e.ujf-grenoble.fr:1521:im2ag";

    static final String USER = "bozond";
    static final String PASSWD = "123456789";

    static Connection conn;

    // --------------------------------------------- //
    // ---------- CHANGER FONCTION CAGE ------------ //
    public static void changerFonctionCage(){
        int noCageChoisi = obtenirInt("le numéro de la cage à changer");
        int noCageResultat = -1;
        PreparedStatement pstmt = null;
        ResultSet result = null;

        try{
            pstmt = conn.prepareStatement("SELECT noCage from LesCages where noCage = ?");
            pstmt.setInt(1, noCageChoisi);

            result = pstmt.executeQuery();
            while (result.next()){
                noCageResultat = result.getInt(1);
            }

            if(noCageResultat != -1){
                // Récupération fonction cage
                String fonction = obtenirChaine("la nouvelle fonction de la cage");
                // Application des changements
                pstmt = conn.prepareStatement("UPDATE LesCages SET fonction = ? WHERE noCage = ?");
                pstmt.setString(1, fonction);
                pstmt.setInt(2, noCageResultat);

                pstmt.executeUpdate();

                System.out.println("La cage "+noCageChoisi+" a pour nouvelle fonction " + fonction);
            }
            else{
                System.out.println(" La cage n'existe pas !");
            }

        }catch (SQLException e){
            System.out.println("Erreur à la préparation du statement.");
            afficherException(e);
        }
    }


    public static void afficherCages(){
        PreparedStatement pstmt = null;
        ResultSet result = null;

        try{
            pstmt = conn.prepareStatement("SELECT * from LesCages");
            result = pstmt.executeQuery();

            System.out.println("noCage\tfonction\tnoAlle");
            while (result.next()){
                System.out.println("");
                System.out.print((result.getInt(1)) + "\t");
                System.out.print(result.getString(2) + "\t");
                System.out.print(result.getInt(3) + "\t");
            }
            System.out.println("\n");

        }catch (SQLException e){
            System.out.println("Erreur à la préparation du statement.");
            afficherException(e);
        }
    }

    private static void afficherAnimaux() {
        PreparedStatement pstmt = null;
        ResultSet result = null;

        try{
            pstmt = conn.prepareStatement("SELECT * FROM LesAnimaux");
            result = pstmt.executeQuery();

            System.out.println("nomA\tsexe\ttype_an\tfonction_cage\tpays\tanNais\tnoCage\tnb_maladies");
            while (result.next()){
                System.out.println("");
                System.out.print((result.getString(1)) + "\t");
                System.out.print(result.getString(2) + "\t");
                System.out.print(result.getString(3) + "\t");
                System.out.print(result.getString(4) + "\t");
                System.out.print(result.getString(5) + "\t");
                System.out.print(result.getInt(6) + "\t");
                System.out.print(result.getInt(7) + "\t");
                System.out.print(result.getInt(8) + "\t");
            }
            System.out.println("\n");

        }catch (SQLException e){
            System.out.println("Erreur à la préparation du statement.");
            afficherException(e);
        }
    }

    // --------------------------------------------- //
    // -------- AJOUTER UN NOUVEL ANNIMAL ---------- //
    public static void ajouterNouvelAnimal(){
        PreparedStatement pstmt = null;
        ResultSet result = null;
        boolean isEmpty = true;

        String nom_a, sexe, type_an, fonction_cage, pays;
        ArrayList<String> cages_compatibles;
        int no_cage, nb_maladies, an_nais, cage_choisie;

        /*
           nom_a = obtenirChaine("le nom de l'animal");
           sexe = obtenirChaine("le sexe de l'animal");
           type_an = obtenirChaine("le type d'animaux");
           fonction_cage = obtenirChaine("la fonction de la cage");
           pays = obtenirChaine("le pays de l'animal");
        //no_cage = obtenirInt("le numero de la cage");
        nb_maladies = obtenirInt("le nombre de maladies");
        an_nais = obtenirInt("l'annee de naissance");
        */

        // a supp :
        nom_a = "Bob";
        sexe = "male";
        type_an = "hélicopter";
        fonction_cage = "fauve";
        pays = "Bwi";
        nb_maladies = 666;
        an_nais = 1815;

        if (afficherCageCompatibles(fonction_cage)){
            System.out.println("Aucune cage compatible.");
        }else{
            cage_choisie = obtenirInt("le num de la cage choisie");

            try{
                pstmt = conn.prepareStatement("INSERT INTO LesAnimaux VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                pstmt.setString(1, nom_a);
                pstmt.setString(2, sexe);
                pstmt.setString(3, type_an);
                pstmt.setString(4, fonction_cage);
                pstmt.setString(5, pays);
                pstmt.setInt(6, an_nais);
                pstmt.setInt(7, cage_choisie);
                pstmt.setInt(8, nb_maladies);
                result = pstmt.executeQuery();

                System.out.println("Animal ajouté avec succès.");

            }catch (SQLException e){
                System.out.println("Erreur à la préparation du statement.");
                afficherException(e);
            }catch (SQLIntegrityConstraintViolationException e){
                System.out.println("Erreur à l'ajout : l'animal doit être né après 1900 inclus et ne peut être que de sexe male, femelle ou hermaphrodite.");
            }

            //TODO: prendre en compte les echecs (anné inférieure à 1900, sexe != normal ....
        }
    }


    // -------------------------------------------- //
    // -------- DEPLACER NOUVEL ANNIMAL ---------- //
    public static void deplacerAnimalVersCage(){
        String nomA = obtenirChaine("l'animal a déplacer");
        String fonction = null;
        PreparedStatement pstmt = null;
        ResultSet result = null;
        int noCage = 0;
        boolean isEmpty = true;

        try{
            // Récupération numéro et fonction de la cage occupé par l'annimal
            pstmt = conn.prepareStatement("SELECT noCage, fonction_cage FROM LesAnimaux WHERE nomA = ?");
            pstmt.setString(1, nomA);
            result = pstmt.executeQuery();
            result.next();
            noCage = result.getInt(1);
            fonction = result.getString(2);

            // Récupération des cage compatible avec l'annimal
            pstmt = conn.prepareStatement("SELECT * FROM LesCages WHERE fonction = ? AND noCage != ?");
            pstmt.setString(1, fonction);
            pstmt.setInt(2, noCage);
            result = pstmt.executeQuery();
            System.out.println("noCage\tfonction\tnoAlle");
            while(result.next()){
                System.out.print((result.getInt(1)) + "\t");
                System.out.print(result.getString(2) + "\t");
                System.out.print(result.getInt(3) + "\t");
                isEmpty = false;
            }
            System.out.println("\n");


            if(isEmpty){
                System.out.println("Déplacement impossible, pas de cage correspondante");
            }
            else{
                // Mise à jour de la cage de l'annimal
                noCage = obtenirInt("la cage ou déplacer l'animal ?");
                pstmt = conn.prepareStatement("UPDATE LesAnimaux SET noCage = ? WHERE nomA = ?");
                pstmt.setInt(1, noCage);
                pstmt.setString(2, nomA);
                pstmt.executeUpdate();


                System.out.println("Animal déplacé avec succès.");
            }

        }catch (SQLException e){
            System.out.println("Erreur à la préparation du statement.");
            afficherException(e);
        }
    }





    // --------------------------------------------- //
    // ----------------- AUTRES -------------------- //
    private static void afficherException(SQLException e){
        System.err.println("failed");
        System.out.println("Affichage de la pile d'erreur");
        e.printStackTrace(System.err);
        System.out.println("Affichage du message d'erreur");
        System.out.println(e.getMessage());
        System.out.println("Affichage du code d'erreur");
        System.out.println(e.getErrorCode());
    }

    private static String obtenirChaine(String param_name){
        System.out.print("Veuillez entrez " + param_name + " :");
        System.out.flush();
        String s = LectureClavier.lireChaine();
        return s;
    }

    private static int obtenirInt(String param_name){
        System.out.flush();
        int i = LectureClavier.lireEntier("Veuillez entrez " + param_name + " :");
        return i;
    }

    private static void menuAffichage(){
        boolean quitter = false;
        int choix = 0;
        while (!quitter){
            System.out.println("=============================");
            System.out.println("===== CHOIX AFFICHAGES ======");
            System.out.println("1 : Afficher les cages");
            System.out.println("2 : Afficher les animaux ");
            System.out.println("99 : Quitter ce menu");
            System.out.println("=============================");

            choix = obtenirInt("le choix du menu");

            switch(choix){
                case 1:
                    afficherCages();
                    break;
                case 2:
                    afficherAnimaux();
                    break;
                case 99:
                    quitter = true;
                    break;
                default:
                    System.out.println("Numéro innexistant !");
                    break;
            }
        }
    }


    private static boolean afficherCageCompatibles(String fonction){
        PreparedStatement pstmt = null;
        ResultSet result = null;
        boolean isEmpty = true;

        try{
            pstmt = conn.prepareStatement("SELECT * from LesCages WHERE fonction = ?");
            pstmt.setString(1, fonction);
            result = pstmt.executeQuery();

            System.out.println("noCage\tfonction\tnoAlle");
            while (result.next()){
                System.out.print((result.getInt(1)) + "\t");
                System.out.print(result.getString(2) + "\t");
                System.out.print(result.getInt(3) + "\t");
                isEmpty = false;
            }
            System.out.println("\n");

        }catch (SQLException e){
            System.out.println("Erreur à la préparation du statement.");
            afficherException(e);
        }
        return isEmpty;
    }



    // --------------------------------------------- //
    // ------------------- MAIN -------------------- //
    public static void main(String args[]) {

        try {
            // Enregistrement du driver Oracle
            System.out.print("Loading Oracle driver... ");
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            System.out.println("loaded");

            // Etablissement de la connection
            System.out.print("Connecting to the database... ");
            conn = DriverManager.getConnection(CONN_URL,USER,PASSWD);
            System.out.println("connected");

            conn.setTransactionIsolation(TRANSACTION_SERIALIZABLE);
            System.out.println("Connexion level : "+conn.getTransactionIsolation());

            // Desactivation de l'autocommit
            conn.setAutoCommit(false);
            System.out.println("Autocommit disabled");

            // code métier de la fonctionnalité
            // 1.1

            boolean quitter = false;
            while(!quitter){
                System.out.println("=============================");
                System.out.println("============ MENU ===========");
                System.out.println("1 : Menu affichage");
                System.out.println("2 : Changer fonction cage");
                System.out.println("3 : Ajouter nouvel animal");
                System.out.println("4 : Déplacer animal");
                System.out.println("5 : ? ");
                System.out.println("99 : Quitter et sauvegarder");
                System.out.println("=============================");
                int monChoix = obtenirInt("le choix du menu");
                switch(monChoix){
                    case 1:
                        menuAffichage();
                        break;
                    case 2:
                        changerFonctionCage();
                        afficherCages();
                        break;
                    case 3:
                        ajouterNouvelAnimal();
                        break;
                    case 4:
                        deplacerAnimalVersCage();
                        break;
                    case 5:

                        break;
                    case 99:
                        quitter = true;
                        break;
                    default:
                        System.out.println("Numéro inexistant !");
                        break;
                }

            }

            // Liberation des ressources et fermeture de la connexion...
            // A COMPLETER
            conn.close();

            System.out.println("bye.");

            // traitement d'exception
        } catch (SQLException e) {
            afficherException(e);
        } finally {
            try {
                if (conn != null) conn.close ();
            }
            catch (SQLException e) {
                e.printStackTrace ();
            }
        }
    }
}
