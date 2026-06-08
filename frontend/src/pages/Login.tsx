import { useState } from "react";
import type { LoginForm } from "@/interfaces/LoginForm.ts";
import { login } from "@/auth/auth.ts";
import { useNavigate } from "react-router-dom";
import SubmitButton from "@/components/SubmitButton";
import ErrorMessageLabel from "@/components/ErrorMessageLabel";
const Login = () => {
  const [form, setForm] = useState<LoginForm>({
    emailAddress: "",
    password: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleChange =
    (field: keyof LoginForm) => (e: React.ChangeEvent<HTMLInputElement>) => {
      setForm((prev) => ({
        ...prev,
        [field]: e.target.value,
      }));
    };

  const submit = async (e: React.SubmitEvent) => {
    e.preventDefault();

    setLoading(true);
    setError("");

    try {
      const res = await login(form);

      localStorage.setItem("accessToken", res.accessToken);
      localStorage.setItem("refreshToken", res.refreshToken);

      // TODO: add default private path
    } catch {
      setError("Invalid email or password.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-100">
      <form
        onSubmit={submit}
        className="w-full max-w-sm rounded-xl bg-white p-8 shadow-lg"
      >
        <h1 className="mb-6 text-center text-2xl font-bold text-slate-900">
          Sign In
        </h1>

        <div className="space-y-4">
          <input
            type="email"
            placeholder="Email"
            value={form.emailAddress}
            onChange={handleChange("emailAddress")}
            required
            className="w-full rounded-lg border border-slate-300 px-4 py-2 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-200"
          />

          <input
            type="password"
            placeholder="Password"
            value={form.password}
            onChange={handleChange("password")}
            required
            className="w-full rounded-lg border border-slate-300 px-4 py-2 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-200"
          />
        </div>

        <ErrorMessageLabel error={error} />

        <SubmitButton
          displayedText={loading ? "Joining..." : "Join organization"}
          isEnabled={loading}
        />
      </form>
    </div>
  );
};
export default Login;
