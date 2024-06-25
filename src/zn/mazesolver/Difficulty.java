package zn.mazesolver;

public enum Difficulty {
    Easy(10, 10, 3,20,2,1),
    Medium(20, 20, 6,30,5,1.5),
    Hard(30, 30, 15,50,10,2);

    private final int rows;
    private final int columns;
    private final int monsterNumber;
    private final int monsterHealth;
    private final int monsterDamage;
    private final double monsterSpeed;


    Difficulty(int rows, int columns, int monsterNumber,int monsterHealth,int monsterDamage, double monsterSpeed) {
        this.rows = rows;
        this.columns = columns;
        this.monsterNumber = monsterNumber;
        this.monsterHealth = monsterHealth;
        this.monsterDamage = monsterDamage;
        this.monsterSpeed = monsterSpeed;

    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getMonsterNumber() {
        return monsterNumber;
    }

    public int getMonsterHealth() {
        return monsterHealth;
    }

    public int getMonsterDamage() {
        return monsterDamage;
    }

    public int getMonsterSpeed() {
        return (int) (1000 - ((monsterSpeed - 1) * 500));
    }




}
