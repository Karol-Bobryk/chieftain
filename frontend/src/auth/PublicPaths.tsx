import { Navigate, Outlet } from "react-router-dom";

const PublicRoute = () => {
  const accessToken = localStorage.getItem("accessToken");

  if (!accessToken) {
    return <Outlet />;
  }

  return <Navigate to={"/join"} />;
  // TODO: add default private path
};

export default PublicRoute;
