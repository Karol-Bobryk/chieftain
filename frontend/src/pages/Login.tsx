import { useState } from "react";
import type { LoginForm } from "@/interfaces/LoginForm.ts";
import { login } from "@/auth/auth.ts";
import SubmitButton from "@/components/SubmitButton";
import ErrorMessageLabel from "@/components/ErrorMessageLabel";
import TextInput from "@/components/TextInput";
import { Link, useNavigate } from "react-router-dom";
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

  const submit = async (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();

    setLoading(true);
    setError("");

    try {
      await login(form);
      navigate("/home");
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
          <TextInput
            type="email"
            placeholder="Email"
            value={form.emailAddress}
            onChange={handleChange("emailAddress")}
            required
          />

          <TextInput
            type="password"
            placeholder="Password"
            value={form.password}
            onChange={handleChange("password")}
            required
          />

          <ErrorMessageLabel error={error} />

          <SubmitButton
            displayedText={loading ? "Joining..." : "Join organization"}
            isEnabled={!loading}
          />
          <p className="pt-2 text-center text-sm text-slate-500">
            Don't have an account yet?{" "}
            <Link
              to="/join"
              className="font-medium text-slate-700 underline underline-offset-4 hover:text-slate-900"
            >
              Create an account
            </Link>
            <br />
            or{" "}
            <Link
              to="/create-org"
              className="font-medium text-slate-700 underline underline-offset-4 hover:text-slate-900"
            >
              create an organization
            </Link>
            .
          </p>
        </div>
      </form>
    </div>
  );
};
export default Login;
