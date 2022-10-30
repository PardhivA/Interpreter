package ATM;

interface Withdraw { // to set max limit that can be withdrawn
    static final double max_limit = 20000.00;
    void CalDen(double mant);
}
