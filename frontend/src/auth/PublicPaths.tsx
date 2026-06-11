import { useAuthStore } from "@/store/auth.store";
import { Navigate, Outlet } from "react-router-dom";

const PublicRoute = () => {
  const accessToken = useAuthStore.getState().accessToken;

  if (!accessToken) {
    return <Outlet />;
  }

  return <Navigate to={"/join"} />;
  // TODO: add default private path
};

export default PublicRoute;
