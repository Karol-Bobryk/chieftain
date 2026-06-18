import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "@/pages/Login";
import JoinOrganization from "@/pages/JoinOrganization";
import CreateOrganization from "@/pages/CreateOrganization";
import CreateGroup from "@/pages/CreateGroup";
import AcceptUser from "@/pages/AcceptUser";
import ProtectedRoute from "@/auth/ProtectedRoute";
import PublicRoute from "@/auth/PublicPaths";
import NotFound from "@/pages/NotFound";
import LandingPage from "@/pages/LandingPage";
import Groups from "@/pages/Groups";
import GroupDetails from "@/pages/GroupDetails";
import AdminPanel from "@/pages/AdminPanel";

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
          <Route path="/home" element={<LandingPage />} />
          <Route path="/create-group" element={<CreateGroup />} />
          <Route path="/accept-user" element={<AcceptUser />} />
          <Route path="/groups" element={<Groups />} />
          <Route path="/group/:groupId" element={<GroupDetails />} />
          <Route path="/admin" element={<AdminPanel />} />
        </Route>

        <Route path="/not-found" element={<NotFound />} />
        <Route path="*" element={<Navigate to="/not-found" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
