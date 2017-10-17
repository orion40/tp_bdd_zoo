/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package tp.zoo.bdd;

import java.sql.*;
import java.util.ArrayList;

public class TpZooBdd {
    
    static final String CONN_URL = "jdbc:oracle:thin:@im2ag-oracle.e.ujf-grenoble.fr:1521:im2ag";
    
    static final String USER = "muratonf";
    static final String PASSWD = "franck";
    
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
                result = null;
                // Récupération fonction cage
                String fonction = obtenirChaine("la nouvelle fonction de la cage");
                // Application des changements
                pstmt = conn.prepareStatement("UPDATE LesCages SET fonction = ? WHERE noCage = ?");
                pstmt.setString(1, fonction);
                pstmt.setInt(2, noCageResultat);
                
                result = pstmt.executeQuery();
                
                if( result != null ){ // l'update ce passe bien
                    System.out.println("La cage "+noCageChoisi+" a pour nouvelle fonction " + fonction);
                }
                else{
                    System.out.println(" La requete s'est mal passé. Rien n'a été modifié.");
                }
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
    
    
    
    // --------------------------------------------- //
    // -------- AJOUTER UN NOUVEL ANNIMAL ---------- //
    public static void ajouterNouvelAnimal(){
        // demander caracs nomA, sexe, type an, fonction cage, pays, anNais, noCage, nb maladies
        // 5 strings, 1 int (4), 1 int(3) non null, int (3),
        String nom_a, sexe, type_an, fonction_cage, pays;
        ArrayList<String> cages_compatibles;
        int no_cage, nb_maladies, an_nais, cage_choisie;
        nom_a = obtenirChaine("le nom de l'animal");
        sexe = obtenirChaine("le sexe de l'animal");
        type_an = obtenirChaine("le type d'animaux");
        fonction_cage = obtenirChaine("la fonction de la cage");
        pays = obtenirChaine("le pays de l'animal");
        //no_cage = obtenirInt("le numero de la cage");
        nb_maladies = obtenirInt("le nombre de maladies");
        an_nais = obtenirInt("l'annee de naissance");
        
        if (!afficherCageCompatibles(fonction_cage)){
            System.out.println("Aucune cage compatible.");
            return;
        }
        
        cage_choisie = obtenirInt("le num de la cage choisie");
                
        // permettre de choisir cage ou il sera loge
        choisirCageParmisListe(cage_choisie);
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
                System.out.println("");
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
    
    private static String choisirCageParmisListe(int cages_compatibles){
        
        return null;
    }
    
    
    
    // -------------------------------------------- //
    // -------- DEPLACER NOUVEL ANNIMAL ---------- //
    public static void deplacerAnimalVersCage(){
        
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
            
            // Desactivation de l'autocommit
            conn.setAutoCommit(false);
            System.out.println("Autocommit disabled");
            
            // code métier de la fonctionnalité
            // 1.1
            
            boolean quitter = false;
            while(!quitter){
                System.out.println("=============================");
                System.out.println("============ MENU ===========");
                System.out.println("1 : Quitter");
                System.out.println("2 : Changer fonction cage");
                System.out.println("3 : Ajouter nouvel animal");
                System.out.println("4 : Afficher les cages");
                System.out.println("=============================");
                int monChoix = obtenirInt("Que voulez vous faire maître ?");
                switch(monChoix){
                    case 1:
                        quitter = true;
                        break;
                    case 2:
                        changerFonctionCage();
                        afficherCages();
                        break;
                    case 3:
                        ajouterNouvelAnimal();
                        break;
                    case 4:
                        afficherCages();
                        break;
                    default:
                        System.out.println("Numéro innexistant !");
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
        }
    }
}
