import ErrorMessageLabel from "@/components/ErrorMessageLabel";
import SubmitButton from "@/components/SubmitButton";
import TextInput from "@/components/TextInput";
import axios from "axios";
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

const CreateOrganization = () => {
  const [form, setForm] = useState({
    name: "",
    surname: "",
    emailAddress: "",
    password: "",
    jobTitle: "",
    organizationName: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleChange =
    (field: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement>) => {
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
      const res = await axios.post("/auth/user/create-with-organization", form);

      if (res.status !== 201) throw new Error();
      navigate("/");
    } catch {
      setError("Error creating organization");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-zinc-50 px-6">
      <form
        onSubmit={submit}
        className="w-full max-w-lg space-y-5 rounded-3xl border border-zinc-200 bg-white p-8"
      >
        <div className="space-y-1">
          <h1 className="text-2xl font-semibold tracking-tight text-zinc-900">
            Create organization
          </h1>
          <p className="text-sm text-zinc-500">
            Enter your details to register a new organization.
          </p>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <TextInput
            type="text"
            placeholder="Name"
            value={form.name}
            onChange={handleChange("name")}
            required
          />

          <TextInput
            type="text"
            placeholder="Surname"
            value={form.surname}
            onChange={handleChange("surname")}
            required
          />
        </div>

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

        <TextInput
          type="text"
          placeholder="Job Title"
          value={form.jobTitle}
          onChange={handleChange("jobTitle")}
          required
        />

        <TextInput
          type="text"
          placeholder="New Organization Name"
          value={form.organizationName}
          onChange={handleChange("organizationName")}
          required
        />

        <ErrorMessageLabel error={error} />

        <SubmitButton
          displayedText={loading ? "Creating..." : "Create organization"}
          isEnabled={!loading}
        />
        <p className="pt-2 text-center text-sm text-slate-500">
          Want to join an existing organization instead?{" "}
          <Link
            to="/join"
            className="font-medium text-slate-700 underline underline-offset-4 transition hover:text-slate-900"
          >
            Join with an invitation
          </Link>
          .
        </p>
      </form>
    </div>
  );
};

export default CreateOrganization;
