import { BrowserRouter, Routes, Route, Link, Navigate} from "react-router-dom";
import Login from "@/pages/Login";
import JoinOrganization from "@/pages/JoinOrganization";
import CreateOrganization from "@/pages/CreateOrganization";
import CreateGroup from "@/pages/CreateGroup";
import AcceptUser from "@/pages/AcceptUser";
import ProtectedRoute from "@/auth/ProtectedRoute";
import PublicRoute from "@/auth/PublicPaths";
import NotFound from "@/pages/NotFound"

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<PublicRoute />}>
          <Route path="/" element={<Login />} />
          <Route path="/join" element={<JoinOrganization />} />
          <Route path="/create-org" element={<CreateOrganization />} />
        </Route>
        <Route element={<ProtectedRoute />}>
          <Route path="/create-group" element={<CreateGroup />} />
          <Route path="/accept-user" element={<AcceptUser />} />
        </Route>

        <Route path="/not-found" element={<NotFound />} />
        <Route path="*" element={<Navigate to="/not-found" replace/>} />
      </Routes>
    </BrowserRouter>
  );
}
