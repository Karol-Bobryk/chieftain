const getCurrentWeekRange = (weekNumber: number) => {
  const now = new Date();
  now.setDate(now.getDate() - weekNumber * 7);

  const day = now.getDay();

  const diffToMonday = (day + 6) % 7;

  const startOfWeek = new Date(now);
  startOfWeek.setDate(now.getDate() - diffToMonday);
  startOfWeek.setHours(0, 0, 0, 0);

  const endOfWeek = new Date(startOfWeek);
  endOfWeek.setDate(startOfWeek.getDate() + 6);
  endOfWeek.setHours(23, 59, 59, 999);

  return {
    periodStart: startOfWeek.toISOString(),
    periodEnd: endOfWeek.toISOString(),
  };
};

export default getCurrentWeekRange;
