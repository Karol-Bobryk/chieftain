import type { RootTaskDisplayDTO } from "@/interfaces/RootTaskDisplayDTO";

export class TaskPositional {
  rootTask: RootTaskDisplayDTO;
  top: number;
  durationHours: number;
  height: number;
  left: number;
  width: number;

  constructor(rootTask: RootTaskDisplayDTO, columnWidth: number) {
    this.rootTask = rootTask;
    this.top = 0;
    this.durationHours = 0;
    this.height = 0;
    this.left = 0;
    this.width = 0;

    this.updateDuration();

    this.updateRight();
    this.updateTop();
    this.width = columnWidth;

    this.updateLeft(columnWidth);
  }

  updateLeft(columnWidth: number) {
    const TIME_COLUMN_WIDTH = 80;
    this.left =
      TIME_COLUMN_WIDTH + this.getDates().startDate.getDay() * columnWidth;
  }

  updateRight() {
    const HOUR_HEIGHT = 64;
    this.height = this.durationHours * HOUR_HEIGHT;
  }

  updateDuration() {
    const { startDate, endDate } = this.getDates();
    this.durationHours =
      endDate.getHours() +
      endDate.getMinutes() / 60 -
      (startDate.getHours() + startDate.getMinutes() / 60);
  }

  updateTop() {
    const HOUR_HEIGHT = 64;
    const { startDate } = this.getDates();
    this.top =
      (startDate.getHours() + startDate.getMinutes() / 60) * HOUR_HEIGHT;
  }

  getDates(): { startDate: Date; endDate: Date } {
    return {
      startDate: new Date(this.rootTask.startedAt),
      endDate: new Date(this.rootTask.deadline),
    };
  }
}
