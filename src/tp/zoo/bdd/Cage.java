package tp_bdd_zoo;

/**
 *
 * @author whoami
 */
public class Cage {
// TODO: generate getters/setters with netbeans
    int noCage = 0;
    String fonction = new String();
    int noAllee = 0; // not null
    /*
	constraint LesCages_C1 primary key (noCage),
	constraint LesCages_C2 check (noCage between 1 and 999),
	constraint LesCages_C3 check (noAllee between 1 and 999)
    */
}

