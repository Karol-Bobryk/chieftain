interface PaginationCartProps {
  pageNumber: number;
  setPageNumber: React.Dispatch<React.SetStateAction<number>>;
  totalPages: number;
}

const PaginationCart = ({
  pageNumber,
  setPageNumber,
  totalPages,
}: PaginationCartProps) => {
  return (
    <div className="flex items-center justify-between rounded-3xl border border-zinc-200 bg-white p-6">
      <button
        disabled={pageNumber === 0}
        onClick={() => setPageNumber((p) => Math.max(0, p - 1))}
        className="rounded-xl border border-zinc-200 px-4 py-2 text-sm font-medium text-zinc-700 transition hover:bg-zinc-50 disabled:cursor-not-allowed disabled:opacity-50"
      >
        Previous
      </button>

      <span className="text-sm text-zinc-500">
        Page {pageNumber + 1} of {totalPages}
      </span>

      <button
        disabled={pageNumber + 1 >= totalPages}
        onClick={() => setPageNumber((p) => p + 1)}
        className="rounded-xl border border-zinc-200 px-4 py-2 text-sm font-medium text-zinc-700 transition hover:bg-zinc-50 disabled:cursor-not-allowed disabled:opacity-50"
      >
        Next
      </button>
    </div>
  );
};

export default PaginationCart;
