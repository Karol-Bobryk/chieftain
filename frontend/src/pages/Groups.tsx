import { useEffect, useState } from "react";
import { api } from "@/auth/axios";
import ErrorMessageLabel from "@/components/ErrorMessageLabel";
import type { GroupDisplayDTO } from "@/interfaces/GroupDisplayDTO";
import GroupListHeaders from "@/components/GroupListHeader";
import GroupCart from "@/components/GroupCart";
import PaginationCart from "@/components/PaginationCart";

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
      <GroupListHeaders
        value={pageSize}
        onChange={(e) => {
          setPageSize(Number(e.target.value));
          setPageNumber(0);
        }}
      />

      <ErrorMessageLabel error={errorMessage} />

      {loading && (
        <div className="rounded-3xl border border-zinc-200 mt-4 bg-white p-8 text-center text-zinc-500">
          Loading groups...
        </div>
      )}
      {!loading && groupPage && groupPage.content.length === 0 && (
        <div className="rounded-3xl border border-zinc-200 bg-white p-12 text-center">
          <h2 className="text-lg font-semibold text-zinc-900">No groups yet</h2>

          <p className="mt-2 text-sm text-zinc-500">
            Create your first group to start organizing members.
          </p>
        </div>
      )}

      {!loading &&
        groupPage?.content.map((group) => (
          <GroupCart key={group.groupId} {...group} />
        ))}

      {groupPage && groupPage.page.totalPages > 0 && (
        <PaginationCart
          pageNumber={pageNumber}
          setPageNumber={setPageNumber}
          totalPages={groupPage.page.totalPages}
        />
      )}
    </div>
  );
};

export default Groups;
