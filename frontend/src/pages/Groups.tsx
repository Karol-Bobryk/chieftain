import { useEffect, useState } from "react";
import { api } from "@/auth/axios";
import ErrorMessageLabel from "@/components/ErrorMessageLabel";

interface GroupDisplayDTO {
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

interface PageGroupDisplayResponse {
  content: GroupDisplayDTO[];
  page: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  };
}

const Groups = () => {
  const [groupPage, setGroupPage] = useState<PageGroupDisplayResponse>();

  const [pageSize, setPageSize] = useState(10); // TODO:add remembering in the future
  const [pageNumber, setPageNumber] = useState(0);
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const fetchGroups = async () => {
    try {
      setLoading(false);
      const res = await api.get<PageGroupDisplayResponse>("/api/users/groups", {
        params: { size: pageSize, page: pageNumber },
      });
      setGroupPage(res.data);
    } catch {
      setErrorMessage("Failed to fetch groups");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchGroups();
  }, [pageNumber, pageSize]);

  return (
    <div className="min-h-screen bg-zinc-50 px-6 py-12">
      <div className="mx-auto max-w-5xl space-y-6">
        <div className="rounded-3xl border border-zinc-200 bg-white p-8">
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h1 className="text-2xl font-semibold tracking-tight text-zinc-900">
                Groups
              </h1>
              <p className="text-sm text-zinc-500">
                Browse organization groups and members.
              </p>
            </div>

            <div className="flex items-center gap-3">
              <span className="text-sm text-zinc-500">Page size</span>

              <select
                value={pageSize}
                onChange={(e) => setPageSize(Number(e.target.value))}
                className="rounded-xl border border-zinc-200 bg-white px-3 py-2 text-sm text-zinc-700 outline-none transition focus:border-zinc-300"
              >
                <option value={10}>10</option>
                <option value={20}>20</option>
                <option value={50}>50</option>
              </select>
            </div>
          </div>
        </div>

        <ErrorMessageLabel error={errorMessage} />

        {loading && (
          <div className="rounded-3xl border border-zinc-200 bg-white p-8 text-center text-zinc-500">
            Loading groups...
          </div>
        )}
        {!loading && groupPage && groupPage.content.length === 0 && (
          <div className="rounded-3xl border border-zinc-200 bg-white p-12 text-center">
            <h2 className="text-lg font-semibold text-zinc-900">
              No groups yet
            </h2>

            <p className="mt-2 text-sm text-zinc-500">
              Create your first group to start organizing members.
            </p>
          </div>
        )}
        {!loading &&
          groupPage?.content.map((group) => (
            <div
              key={group.groupId}
              className="rounded-3xl border border-zinc-200 bg-white p-6"
            >
              <div className="border-b border-zinc-100 pb-4">
                <h2 className="text-lg font-semibold text-zinc-900">
                  {group.name}
                </h2>

                <p className="mt-1 text-sm text-zinc-500">
                  {group.members.length} member
                  {group.members.length !== 1 ? "s" : ""}
                </p>
              </div>

              <div className="mt-4 space-y-3">
                {group.members.map((member) => (
                  <div
                    key={member.userId}
                    className="flex items-center justify-between rounded-xl border border-zinc-100 bg-zinc-50 px-4 py-3"
                  >
                    <div>
                      <p className="font-medium text-zinc-900">
                        {member.name} {member.surname}
                      </p>
                      <p className="text-sm text-zinc-500">{member.userId}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          ))}

        {groupPage && groupPage.page.totalPages > 0 && (
          <div className="flex items-center justify-between rounded-3xl border border-zinc-200 bg-white p-6">
            <button
              disabled={pageNumber === 0}
              onClick={() => setPageNumber((p) => Math.max(0, p - 1))}
              className="rounded-xl border border-zinc-200 px-4 py-2 text-sm font-medium text-zinc-700 transition hover:bg-zinc-50 disabled:cursor-not-allowed disabled:opacity-50"
            >
              Previous
            </button>

            <span className="text-sm text-zinc-500">
              Page {groupPage.page.number + 1} of {groupPage.page.totalPages}
            </span>

            <button
              disabled={pageNumber + 1 >= groupPage.page.totalPages}
              onClick={() => setPageNumber((p) => p + 1)}
              className="rounded-xl border border-zinc-200 px-4 py-2 text-sm font-medium text-zinc-700 transition hover:bg-zinc-50 disabled:cursor-not-allowed disabled:opacity-50"
            >
              Next
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default Groups;
