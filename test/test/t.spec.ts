import axios from "axios";

axios.defaults.baseURL = "http://localhost:8001"


describe("aliveScript tests", () => {

  it('should ignore comments', () => {

    const lines = ""

    expect(Promise.resolve(axios.post("/compile/", {lines})))

  });

  
})
