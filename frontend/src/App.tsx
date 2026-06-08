import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import Login from './Login';
import JoinOrganization from './JoinOrganization';
import CreateOrganization from './CreateOrganization';
import CreateGroup from './CreateGroup';
import AcceptUser from './AcceptUser';

export default function App() {
    return (
        <BrowserRouter>
            <div>
                <nav style={{
                    display: 'flex',
                    gap: '15px',
                    padding: '10px',
                    backgroundColor: '#eee',
                    marginBottom: '20px'
                }}>
                    <Link to="/">Login</Link>
                    <Link to="/join">Join Org</Link>
                    <Link to="/create-org">Create Org</Link>
                    <Link to="/create-group">Create Group</Link>
                    <Link to="/accept-user">Accept User</Link>
                </nav>

                <div style={{ padding: '20px' }}>
                    <Routes>
                        <Route path="/" element={<Login />} />
                        <Route path="/join" element={<JoinOrganization />} />
                        <Route path="/create-org" element={<CreateOrganization />} />
                        <Route path="/create-group" element={<CreateGroup />} />
                        <Route path="/accept-user" element={<AcceptUser />} />
                    </Routes>
                </div>
            </div>
        </BrowserRouter>
    );
}