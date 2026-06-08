import { Link } from "react-router-dom";

const Navbar = () => {
  return (
    <nav className="sticky top-0 z-50 border-b border-zinc-200 bg-white/80 backdrop-blur">
      <div className="mx-auto flex max-w-6xl items-center gap-6 px-6 py-4">
        <Link
          to="/"
          className="text-sm font-medium text-zinc-600 transition hover:text-zinc-900"
        >
          Login
        </Link>

        <Link
          to="/join"
          className="text-sm font-medium text-zinc-600 transition hover:text-zinc-900"
        >
          Join Org
        </Link>

        <Link
          to="/create-org"
          className="text-sm font-medium text-zinc-600 transition hover:text-zinc-900"
        >
          Create Org
        </Link>

        <Link
          to="/create-group"
          className="text-sm font-medium text-zinc-600 transition hover:text-zinc-900"
        >
          Create Group
        </Link>

        <Link
          to="/accept-user"
          className="ml-auto text-sm font-medium text-zinc-600 transition hover:text-zinc-900"
        >
          Accept User
        </Link>
      </div>
    </nav>
  );
};
export default Navbar;
