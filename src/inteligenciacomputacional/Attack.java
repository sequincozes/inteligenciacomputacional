package inteligenciacomputacional;

public class Attack {

    private String attackName;
    private int numberAttacks;
    private int numberNormals;
    private Run includeOnTests;

    public Attack(String attackName, int numberAttacks, int numberNormals, Run includeOnTests) {
        this.attackName = attackName;
        this.numberAttacks = numberAttacks;
        this.numberNormals = numberNormals;
        this.includeOnTests = includeOnTests;
    }

    public String getAttackName() {
        return attackName;
    }

    public void setAttackName(String attackName) {
        this.attackName = attackName;
    }

    public boolean isIncludeOnTests() {
        return includeOnTests == Run.INCLUDE;
    }

    public void setIncludeOnTests(Run includeOnTests) {
        this.includeOnTests = includeOnTests;
    }

    public int getNumberAttacks() {
        return numberAttacks;
    }

    public void setNumberAttacks(int numberAttacks) {
        this.numberAttacks = numberAttacks;
    }

    public int getNumberNormals() {
        return numberNormals;
    }

    public void setNumberNormals(int numberNormals) {
        this.numberNormals = numberNormals;
    }
}
