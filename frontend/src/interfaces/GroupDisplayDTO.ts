export interface GroupDisplayDTO {
  groupId: string;
  name: string;
  members: [
    {
      userId: string;
      name: string;
      surname: string;
    },
  ];
}
