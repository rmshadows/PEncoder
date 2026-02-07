package appCtrl;

/**
 * 模拟耗时任务，用于进度条彩蛋（无实际业务）。
 */
class SimulatedActivity implements Runnable {
	private volatile int current;
	private final int total;

	SimulatedActivity(int total) {
		this.total = total;
	}

	int getAmount() {
		return total;
	}

	int getCurrent() {
		return current;
	}

	@Override
	public void run() {
		while (current < total) {
			try {
				Thread.sleep(50L);
			} catch (InterruptedException ignored) {
				Thread.currentThread().interrupt();
			}
			current++;
		}
	}
}
