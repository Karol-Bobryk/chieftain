const LandingPage = () => {
  return (
    <div className="mx-auto flex min-h-[70vh] max-w-5xl items-center justify-center px-6">
      <div className="text-center">
        <h1 className="text-3xl font-semibold text-slate-900">Hello 👋</h1>

        <p className="mt-3 text-lg text-slate-500">
          Welcome back to your organization panel.
        </p>

        <p className="mt-1 text-sm text-slate-400">
          Select an option from the navigation to get started.
        </p>

        <div className="mt-8 inline-flex rounded-full border border-slate-200 bg-white px-4 py-2 text-sm text-slate-600 shadow-sm">
          Everything is up to date
        </div>
      </div>
    </div>
  );
}; // TODO maybe add updates or smth
// PS: corposlorpo

export default LandingPage;
