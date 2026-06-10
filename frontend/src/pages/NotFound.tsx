import { useNavigate } from "react-router-dom";

const NotFound = () => {
  const navigate = useNavigate();
  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-100">
      <div className="border-black p-8 border-2 rounded-lg">
        <h1 className="pb-4">Page not found</h1>
        <button
          onClick={() => navigate("/")}
          className="h-11 w-full rounded-xl bg-zinc-900 text-sm font-medium text-white transition hover:bg-zinc-800 disabled:opacity-50"
        >
          Go Back
        </button>
      </div>
    </div>
  );
};
export default NotFound;