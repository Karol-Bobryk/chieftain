import ErrorMessageLabel from "@/components/ErrorMessageLabel";
import SubmitButton from "@/components/SubmitButton";
import TextInput from "@/components/TextInput";
import { useState } from "react";

const JoinOrganization = () => {
  const [form, setForm] = useState({
    name: "",
    surname: "",
    emailAddress: "",
    password: "",
    jobTitle: "",
    organizationToken: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleChange =
    (field: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement>) => {
      setForm((prev) => ({
        ...prev,
        [field]: e.target.value,
      }));
    };

  const submit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const res = await fetch("/auth/user/create", {
        // TODO: axios please
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });

      if (!res.ok) throw new Error();
    } catch {
      setError("Error joining organization");
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
            Join organization
          </h1>
          <p className="text-sm text-zinc-500">
            Enter your details to continue.
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
          placeholder="Organization Token"
          value={form.organizationToken}
          onChange={handleChange("organizationToken")}
          required
        />

        <ErrorMessageLabel error={error} />

        <SubmitButton
          displayedText={loading ? "Joining..." : "Join organization"}
          isEnabled={loading}
        />
      </form>
    </div>
  );
};

export default JoinOrganization;
