import { useAuthStore } from "@/store/auth.store";
import { Link, useNavigate } from "react-router-dom";

const Navbar = () => {
  const navigate = useNavigate();
  const logOut = () => {
    useAuthStore.getState().clearTokens();
    navigate("/");
  };
  return (
    <nav className="sticky top-0 z-50 border-b border-slate-200 bg-white/80 backdrop-blur">
      <div className="mx-auto flex h-16 max-w-7xl items-center px-6">
        <Link
          to="/"
          className="mr-10 text-lg font-semibold tracking-tight text-slate-900"
        >
          Chieftain
        </Link>

        {/* Navigation */}
        <div className="flex items-center gap-1">
          <Link
            to="/join"
            className="rounded-lg px-3 py-2 text-sm font-medium text-slate-600 transition hover:bg-slate-100 hover:text-slate-900"
          >
            Join Org
          </Link>

          <Link
            to="/create-org"
            className="rounded-lg px-3 py-2 text-sm font-medium text-slate-600 transition hover:bg-slate-100 hover:text-slate-900"
          >
            Create Org
          </Link>

          <Link
            to="/create-group"
            className="rounded-lg px-3 py-2 text-sm font-medium text-slate-600 transition hover:bg-slate-100 hover:text-slate-900"
          >
            Create Group
          </Link>

          <Link
            to="/accept-user"
            className="rounded-lg px-3 py-2 text-sm font-medium text-slate-600 transition hover:bg-slate-100 hover:text-slate-900"
          >
            User Requests
          </Link>
        </div>

        {/* Right side */}
        <div className="ml-auto flex items-center gap-3">
          <button
            onClick={logOut}
            className="rounded-lg px-3 py-2 text-sm font-medium text-slate-500 transition hover:bg-red-50 hover:text-red-600"
          >
            Log out
          </button>
        </div>
      </div>
    </nav>
  );
};
export default Navbar;
