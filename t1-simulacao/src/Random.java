public class Random {
    private int a;
    private int c;
    private double M;
    private double x;

    public Random() {
        this.a = 1664525;
        this.c = 2312321;
        this.M = 483274834;
        this.x = 389213981;
    }

    public double next(){
        x = (a * x + c) % M;
        return x / M;
    }
}
