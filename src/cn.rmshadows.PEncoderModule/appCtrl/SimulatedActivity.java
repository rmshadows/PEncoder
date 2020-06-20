package appCtrl;

/**
 * 无用功能不用在意。
 */
class SimulatedActivity implements Runnable {
	private volatile int current;
	 	private int amount;
	public SimulatedActivity(int paramInt) {
		this.current = 0;
		this.amount = paramInt;
	}
	public int getAmount() {
		return this.amount;
	}
	public int getCurrent() {
		return this.current;
	}
	public void run() {
		while (this.current < this.amount) {
			try {
				Thread.sleep(50L);
			} catch (InterruptedException interruptedException) {}
			this.current++;
		}
	}
}
