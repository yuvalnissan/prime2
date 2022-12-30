
export interface Data {
    id: string,
    type: string,
    val: string,
    expressions?: Expression[]
}

export interface Expression extends Data {
    negative?: boolean
}
  